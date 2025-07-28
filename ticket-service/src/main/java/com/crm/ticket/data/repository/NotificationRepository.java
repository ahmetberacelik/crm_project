package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
    Flux<Notification> findByTicketId(Long ticketId);
} 