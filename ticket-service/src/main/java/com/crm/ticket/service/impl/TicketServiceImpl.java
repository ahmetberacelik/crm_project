package com.crm.ticket.service.impl;

import com.crm.ticket.api.dto.request.CreateTicketEmployeeRequest;
import com.crm.ticket.api.dto.request.CreateTicketPublicRequest;
import com.crm.ticket.api.dto.response.TicketResponse;
import com.crm.ticket.data.entity.Customer;
import com.crm.ticket.data.entity.Ticket;
import com.crm.ticket.data.entity.TicketAssignment;
import com.crm.ticket.data.entity.TicketCategory;
import com.crm.ticket.data.repository.CustomerRepository;
import com.crm.ticket.data.repository.TicketAssignmentRepository;
import com.crm.ticket.data.repository.TicketCategoryRepository;
import com.crm.ticket.data.repository.TicketRepository;
import com.crm.ticket.infrastructure.client.DepartmentClient;
import com.crm.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final TicketAssignmentRepository ticketAssignmentRepository;
    private final DepartmentClient departmentClient;
    private final TicketCategoryRepository ticketCategoryRepository;

    private static final Long DEFAULT_DEPARTMENT_ID = 14L; // Bilgi Teknolojileri Destek

    @Override
    @Transactional
    public Mono<TicketResponse> createTicketFromCustomer(CreateTicketPublicRequest request) {
        // 1. Müşteriyi e-postasına göre bul veya yeni oluştur
        Mono<Customer> customerMono = customerRepository.findByEmail(request.getEmail())
                .switchIfEmpty(customerRepository.save(
                        Customer.builder()
                                .name(request.getName())
                                .surname(request.getSurname())
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .build()
                ));

        // Kategoriye göre departman ID'sini asenkron olarak al
        Mono<Long> departmentIdMono = determineDepartmentIdByCategory(request.getCategory());

        return Mono.zip(customerMono, departmentIdMono)
                .flatMap(tuple -> {
                    Customer customer = tuple.getT1();
                    Long departmentId = tuple.getT2();

                    Ticket newTicket = Ticket.builder()
                            .issue(request.getIssue())
                            .priority(request.getPriority())
                            .isActive(true)
                            .createdDate(LocalDateTime.now())
                            .isEmployee(false)
                            .creatorId(customer.getId())
                            .build();

                    return ticketRepository.save(newTicket)
                            .flatMap(savedTicket -> createInitialAssignment(savedTicket, departmentId));
                })
                .flatMap(this::buildTicketResponse);
    }

    @Override
    @Transactional
    public Mono<TicketResponse> createTicketFromEmployee(CreateTicketEmployeeRequest request, Long creatorId) {
        Mono<Long> departmentIdMono = determineDepartmentIdByCategory(request.getCategory());

        return departmentIdMono.flatMap(departmentId -> {
            Ticket newTicket = Ticket.builder()
                    .issue(request.getIssue())
                    .priority(request.getPriority())
                    .isActive(true)
                    .createdDate(LocalDateTime.now())
                    .isEmployee(true)
                    .creatorId(creatorId)
                    .build();

            return ticketRepository.save(newTicket)
                    .flatMap(savedTicket -> createInitialAssignment(savedTicket, departmentId));
        }).flatMap(this::buildTicketResponse);
    }

    private Mono<Ticket> createInitialAssignment(Ticket ticket, Long departmentId) {
        TicketAssignment assignment = TicketAssignment.builder()
                .ticketId(ticket.getId())
                .isInPool(true)
                .departmentId(departmentId)
                .assignedDate(LocalDateTime.now())
                .status("OPEN")
                .build();
        return ticketAssignmentRepository.save(assignment).thenReturn(ticket);
    }

    @Override
    public Mono<TicketResponse> getTicketById(Long id) {
        return ticketRepository.findById(id)
                .flatMap(this::buildTicketResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Ticket not found with id: " + id))); // Sonraki adımda bunu iyileştireceğiz
    }

    /**
     * Assembles the full TicketResponse by fetching additional data from other services.
     * This method is now the core of the service's read logic.
     */
    private Mono<TicketResponse> buildTicketResponse(Ticket ticket) {
        // 1. Get creator information (either a customer or an employee)
        Mono<TicketResponse.CreatorInfo> creatorInfoMono = getCreatorInfo(ticket);

        // 2. Get all assignments and enrich them with names from department-service
        Mono<List<TicketResponse.AssignmentInfo>> assignmentsInfoMono = ticketAssignmentRepository.findByTicketId(ticket.getId())
                .flatMap(this::buildAssignmentInfo) // Asynchronously enrich each assignment
                .collectList();

        // 3. Combine all asynchronous results into a single response object
        return Mono.zip(creatorInfoMono, assignmentsInfoMono)
                .map(tuple -> {
                    TicketResponse.CreatorInfo creator = tuple.getT1();
                    List<TicketResponse.AssignmentInfo> assignments = tuple.getT2();

                    return TicketResponse.builder()
                            .id(ticket.getId())
                            .issue(ticket.getIssue())
                            .priority(ticket.getPriority())
                            .isActive(ticket.getIsActive())
                            .createdDate(ticket.getCreatedDate())
                            .closedDate(ticket.getClosedDate())
                            .creator(creator)
                            .assignments(assignments)
                            .build();
                });
    }

    /**
     * Fetches creator info based on whether the creator is an employee or a customer.
     */
    private Mono<TicketResponse.CreatorInfo> getCreatorInfo(Ticket ticket) {
        if (ticket.getIsEmployee()) {
            // If creator is an employee, get person details from department-service
            return departmentClient.getPersonById(ticket.getCreatorId())
                    .map(person -> TicketResponse.CreatorInfo.builder()
                            .id(person.getId())
                            .name(person.getName())
                            .surname(person.getSurname())
                            .email(person.getEmail())
                            .type("EMPLOYEE")
                            .build())
                    .defaultIfEmpty(buildUnknownCreator(ticket.getCreatorId(), "Employee")); // Fallback
        } else {
            // If creator is a customer, get details from local customer table
            return customerRepository.findById(ticket.getCreatorId())
                    .map(customer -> TicketResponse.CreatorInfo.builder()
                            .id(customer.getId())
                            .name(customer.getName())
                            .surname(customer.getSurname())
                            .email(customer.getEmail())
                            .type("CUSTOMER")
                            .build())
                    .defaultIfEmpty(buildUnknownCreator(ticket.getCreatorId(), "Customer")); // Fallback
        }
    }

    /**
     * Enriches a single TicketAssignment with the name of the assignee (person or department).
     */
    private Mono<TicketResponse.AssignmentInfo> buildAssignmentInfo(TicketAssignment assignment) {
        Mono<TicketResponse.AssigneeInfo> assigneeInfoMono;

        if (assignment.getPersonId() != null) {
            // If assigned to a specific person, get their name
            assigneeInfoMono = departmentClient.getPersonById(assignment.getPersonId())
                    .map(person -> TicketResponse.AssigneeInfo.builder()
                            .id(person.getId())
                            .name(person.getName() + " " + person.getSurname())
                            .type("PERSON")
                            .build())
                    .defaultIfEmpty(buildUnknownAssignee(assignment.getPersonId(), "Person")); // Fallback
        } else {
            // If assigned to a department pool, get the department's name
            assigneeInfoMono = departmentClient.getDepartmentById(assignment.getDepartmentId())
                    .map(department -> TicketResponse.AssigneeInfo.builder()
                            .id(department.getId())
                            .name(department.getName())
                            .type("DEPARTMENT_POOL")
                            .build())
                    .defaultIfEmpty(buildUnknownAssignee(assignment.getDepartmentId(), "Department")); // Fallback
        }

        return assigneeInfoMono.map(assigneeInfo ->
                TicketResponse.AssignmentInfo.builder()
                        .assignmentId(assignment.getId())
                        .status(assignment.getStatus())
                        .assignedDate(assignment.getAssignedDate())
                        .completedDate(assignment.getCompletedDate())
                        .assignee(assigneeInfo)
                        .build()
        );
    }

    // Fallback method in case an entity is not found in the other service
    private TicketResponse.CreatorInfo buildUnknownCreator(Long id, String type) {
        return TicketResponse.CreatorInfo.builder().id(id).name("Unknown").surname(type).email("N/A").type(type).build();
    }

    // Fallback method in case an entity is not found in the other service
    private TicketResponse.AssigneeInfo buildUnknownAssignee(Long id, String type) {
        return TicketResponse.AssigneeInfo.builder().id(id).name("Unknown " + type).type(type).build();
    }

    /**
     * Determines the target department ID by looking up the category key in the database.
     * This replaces the static switch-case block.
     *
     * @param categoryKey The category key from the API request.
     * @return A Mono emitting the target department ID, or a default ID if not found.
     */
    private Mono<Long> determineDepartmentIdByCategory(String categoryKey) {
        return ticketCategoryRepository.findByCategoryKeyAndIsActive(categoryKey, true)
                .map(TicketCategory::getTargetDepartmentId)
                .defaultIfEmpty(DEFAULT_DEPARTMENT_ID); // If category not found, return default
    }
}
