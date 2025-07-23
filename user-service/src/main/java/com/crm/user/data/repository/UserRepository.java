package com.crm.user.data.repository;

import com.crm.user.data.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@link User} entity.
 * Provides reactive CRUD operations and custom query methods for 'users' table.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Spring Data R2DBC will automatically generate the query based on the method name.
     *
     * @param email the email address to search for.
     * @return a {@link Mono} emitting the found user or empty if not found.
     */
    Mono<User> findByEmail(String email);
} 