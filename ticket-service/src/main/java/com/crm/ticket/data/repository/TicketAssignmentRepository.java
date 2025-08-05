package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.TicketAssignment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TicketAssignmentRepository extends ReactiveCrudRepository<TicketAssignment, Long> {
    Flux<TicketAssignment> findByTicketId(Long ticketId);
}