package com.crm.user.service;

import com.crm.user.data.entity.User;
import reactor.core.publisher.Flux;

/**
 * Service interface for user management operations.
 */
public interface UserService {

    /**
     * Retrieves all users in the system.
     * This method is intended for administrative purposes.
     *
     * @return A {@link Flux} emitting all found {@link User} objects.
     */
    Flux<User> findAll();

} 