package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.Ticket;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends ReactiveCrudRepository<Ticket, Long> {
}
