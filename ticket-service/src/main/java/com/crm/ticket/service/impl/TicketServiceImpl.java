package com.crm.ticket.service.impl;

import com.crm.ticket.api.dto.request.AssignTicketRequest;
import com.crm.ticket.api.dto.request.CreateTicketRequest;
import com.crm.ticket.api.dto.request.UpdateStatusRequest;
import com.crm.ticket.api.dto.response.TicketResponse;
import com.crm.ticket.data.entity.Notification;
import com.crm.ticket.data.entity.SupportTicket;
import com.crm.ticket.data.entity.TicketHistory;
import com.crm.ticket.data.repository.NotificationRepository;
import com.crm.ticket.data.repository.SupportTicketRepository;
import com.crm.ticket.data.repository.TicketHistoryRepository;
import com.crm.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public Mono<TicketResponse> createTicket(CreateTicketRequest request) {
        SupportTicket ticket = SupportTicket.builder()
                .customerId(request.getCustomerId())
                .issue(request.getIssue())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status("Açık")
                .poolId(request.getPoolId())
                .createdAt(LocalDateTime.now())
                .build();

        return supportTicketRepository.save(ticket)
                .flatMap(saved -> createNotification(saved.getTicketId(), "Ticket created")
                        .thenReturn(toResponse(saved)));
    }

    @Override
    public Mono<TicketResponse> assignTicket(AssignTicketRequest request) {
        return supportTicketRepository.findById(request.getTicketId())
                .switchIfEmpty(Mono.error(new RuntimeException("Ticket not found")))
                .flatMap(ticket -> {
                    TicketHistory history = TicketHistory.builder()
                            .ticketId(ticket.getTicketId())
                            .previousAssignedTo(ticket.getAssignedTo())
                            .newAssignedTo(request.getNewAssignedTo())
                            .reason(request.getReason())
                            .changedAt(LocalDateTime.now())
                            .build();

                    ticket.setAssignedTo(request.getNewAssignedTo());
                    // save operations
                    return ticketHistoryRepository.save(history)
                            .then(supportTicketRepository.save(ticket))
                            .flatMap(saved -> createNotification(saved.getTicketId(), "Ticket assigned")
                                    .thenReturn(toResponse(saved)));
                });
    }

    @Override
    public Mono<TicketResponse> updateStatus(UpdateStatusRequest request) {
        return supportTicketRepository.findById(request.getTicketId())
                .switchIfEmpty(Mono.error(new RuntimeException("Ticket not found")))
                .flatMap(ticket -> {
                    TicketHistory history = TicketHistory.builder()
                            .ticketId(ticket.getTicketId())
                            .previousPoolId(ticket.getPoolId())
                            .newPoolId(ticket.getPoolId())
                            .previousAssignedTo(ticket.getAssignedTo())
                            .newAssignedTo(ticket.getAssignedTo())
                            .reason(request.getReason())
                            .changedAt(LocalDateTime.now())
                            .build();

                    ticket.setStatus(request.getNewStatus());

                    return ticketHistoryRepository.save(history)
                            .then(supportTicketRepository.save(ticket))
                            .flatMap(saved -> createNotification(saved.getTicketId(), "Ticket status updated to " + request.getNewStatus())
                                    .thenReturn(toResponse(saved)));
                });
    }

    @Override
    public Flux<TicketResponse> findByCustomer(Long customerId) {
        return supportTicketRepository.findByCustomerId(customerId)
                .map(this::toResponse);
    }

    private Mono<Notification> createNotification(Long ticketId, String message) {
        Notification notification = Notification.builder()
                .ticketId(ticketId)
                .message(message)
                .sentTo("N/A")
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    private TicketResponse toResponse(SupportTicket ticket) {
        return TicketResponse.builder()
                .ticketId(ticket.getTicketId())
                .customerId(ticket.getCustomerId())
                .issue(ticket.getIssue())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .poolId(ticket.getPoolId())
                .assignedTo(ticket.getAssignedTo())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
} 