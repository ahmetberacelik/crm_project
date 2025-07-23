package com.crm.user.data.repository;

import com.crm.user.data.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@link Customer} entity.
 * Provides reactive CRUD operations and custom query methods for 'customers' table.
 */
@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {

    /**
     * Finds a customer by their email address.
     * Spring Data R2DBC will automatically generate the query based on the method name.
     *
     * @param email the email address to search for.
     * @return a {@link Mono} emitting the found customer or empty if not found.
     */
    Mono<Customer> findByEmail(String email);
} 