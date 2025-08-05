package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    Mono<Customer> findByEmail(String email);
}
