package com.crm.user.service.impl;

import com.crm.user.data.entity.User;
import com.crm.user.data.repository.UserRepository;
import com.crm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Implementation of the UserService interface.
 * Handles business logic for user management.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();
    }
} 