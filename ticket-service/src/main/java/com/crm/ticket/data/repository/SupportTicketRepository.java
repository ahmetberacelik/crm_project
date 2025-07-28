package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.SupportTicket;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SupportTicketRepository extends ReactiveCrudRepository<SupportTicket, Long> {
    Flux<SupportTicket> findByCustomerId(Long customerId);
    Flux<SupportTicket> findByAssignedTo(Long assignedTo);
    Flux<SupportTicket> findByStatus(String status);
} 