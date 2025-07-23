package com.crm.user.service;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
import com.crm.user.api.dto.response.AuthResponse;
import reactor.core.publisher.Mono;

/**
 * Service interface for handling authentication-related operations such as
 * user login and customer registration.
 */
public interface AuthService {

    /**
     * Authenticates a user or customer based on the provided credentials.
     *
     * @param request The login request containing email and password.
     * @return A Mono emitting the authentication response with a JWT upon success.
     */
    Mono<AuthResponse> login(LoginRequest request);

    /**
     * Registers a new customer in the system.
     *
     * @param request The registration request containing customer details.
     * @return A Mono emitting the authentication response with a JWT upon successful registration.
     */
    Mono<AuthResponse> registerCustomer(RegisterRequest request);
} 