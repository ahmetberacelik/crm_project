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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
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

    @Override
    @Transactional
    public Mono<TicketResponse> claimTicket(Long ticketId, Long employeeId) {
        // 1. İlgili, açık ve havuzda olan atamayı bul
        Mono<TicketAssignment> openPoolAssignmentMono = ticketAssignmentRepository
                .findFirstByTicketIdAndStatusAndIsInPool(ticketId, "OPEN", true)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Claimable assignment not found for ticket id: " + ticketId)));

        // 2. Çalışanın detaylarını ve departmanını department-service'ten al
        //    (Token'ı HTTP isteğinden alacak şekilde DÜZELTİLDİ)
        Mono<com.crm.ticket.infrastructure.client.dto.PersonResponse> personMono = Mono.deferContextual(ctx -> {
            ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"));
            }
            String token = authHeader.substring(7);
            return departmentClient.getPersonById(employeeId, token);
        }).switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee details not found for id: " + employeeId)));

        // 3. İki asenkron işlemi birleştir
        return Mono.zip(openPoolAssignmentMono, personMono)
                .flatMap(tuple -> {
                    TicketAssignment assignment = tuple.getT1();
                    var person = tuple.getT2();
                    Long employeeDepartmentId = person.getDepartmentId();

                    // 4. Doğrulama: Çalışan doğru departmanda mı?
                    if (!assignment.getDepartmentId().equals(employeeDepartmentId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Employee cannot claim a ticket from another department's pool."));
                    }

                    // 5. Atamayı güncelle
                    assignment.setIsInPool(false);
                    assignment.setPersonId(employeeId);

                    assignment.setStatus("IN_PROGRESS");

                    return ticketAssignmentRepository.save(assignment);
                })
                // 6. Güncellenmiş ticket'ın tam yanıtını oluşturup dön
                .flatMap(savedAssignment -> getTicketById(savedAssignment.getTicketId()));
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
            if (!ticket.getIsEmployee()) {
                return customerRepository.findById(ticket.getCreatorId())
                        .map(customer -> TicketResponse.CreatorInfo.builder()
                                .id(customer.getId()).name(customer.getName()).surname(customer.getSurname())
                                .email(customer.getEmail()).type("CUSTOMER").build())
                        .defaultIfEmpty(buildUnknownCreator(ticket.getCreatorId(), "Customer"));
            }

            // Context'ten isteğin kendisini (ServerWebExchange) alıyoruz
            return Mono.deferContextual(ctx -> {
                ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
                // İstekten Authorization başlığını ve token'ı alıyoruz
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return Mono.just(buildUnknownCreator(ticket.getCreatorId(), "Employee"));
                }
                String token = authHeader.substring(7);

                // Şimdi token ile client çağrısını yapıyoruz
                return departmentClient.getPersonById(ticket.getCreatorId(), token)
                        .map(person -> TicketResponse.CreatorInfo.builder()
                                .id(person.getPersonId()).name(person.getName()).surname(person.getSurname())
                                .email(person.getEmail()).type("EMPLOYEE").build())
                        .defaultIfEmpty(buildUnknownCreator(ticket.getCreatorId(), "Employee"));
            });
        }

    /**
     * Enriches a single TicketAssignment with the name of the assignee (person or department).
     */
    private Mono<TicketResponse.AssignmentInfo> buildAssignmentInfo(TicketAssignment assignment) {
        // Context'ten isteğin kendisini (ServerWebExchange) alıyoruz
        return Mono.deferContextual(ctx -> {
            ServerWebExchange exchange = ctx.get(ServerWebExchange.class);
            // İstekten Authorization başlığını ve token'ı alıyoruz
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // HATA BURADA DÜZELTİLDİ:
                // Önce bilinmeyen bir AssigneeInfo oluşturuyoruz.
                TicketResponse.AssigneeInfo unknownAssignee = buildUnknownAssignee(
                        assignment.getDepartmentId() != null ? assignment.getDepartmentId() : assignment.getPersonId(),
                        "Unknown"
                );
                // Sonra bunu tam bir AssignmentInfo nesnesinin içine koyup Mono olarak dönüyoruz.
                return Mono.just(TicketResponse.AssignmentInfo.builder()
                        .assignmentId(assignment.getId())
                        .status(assignment.getStatus())
                        .assignedDate(assignment.getAssignedDate())
                        .assignee(unknownAssignee)
                        .build());
            }
            String token = authHeader.substring(7);

            Mono<TicketResponse.AssigneeInfo> assigneeInfoMono;

            if (assignment.getPersonId() != null) {
                assigneeInfoMono = departmentClient.getPersonById(assignment.getPersonId(), token)
                        .map(person -> TicketResponse.AssigneeInfo.builder()
                                .id(person.getPersonId()).name(person.getName() + " " + person.getSurname()).type("PERSON").build())
                        .defaultIfEmpty(buildUnknownAssignee(assignment.getPersonId(), "Person"));
            } else {
                assigneeInfoMono = departmentClient.getDepartmentById(assignment.getDepartmentId(), token)
                        .map(department -> TicketResponse.AssigneeInfo.builder()
                                .id(department.getDepartmentId()).name(department.getName()).type("DEPARTMENT_POOL").build())
                        .defaultIfEmpty(buildUnknownAssignee(assignment.getDepartmentId(), "Department"));
            }

            return assigneeInfoMono.map(assigneeInfo ->
                    TicketResponse.AssignmentInfo.builder()
                            .assignmentId(assignment.getId()).status(assignment.getStatus())
                            .assignedDate(assignment.getAssignedDate()).completedDate(assignment.getCompletedDate())
                            .assignee(assigneeInfo).build()
            );
        });
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
