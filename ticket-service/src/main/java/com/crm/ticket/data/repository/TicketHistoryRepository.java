package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.TicketHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TicketHistoryRepository extends ReactiveCrudRepository<TicketHistory, Long> {
    Flux<TicketHistory> findByTicketId(Long ticketId);
} 