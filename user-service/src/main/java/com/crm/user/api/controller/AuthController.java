package com.crm.user.api.controller;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
import com.crm.user.api.dto.response.AuthResponse;
import com.crm.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for handling authentication endpoints like login and registration.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user/customer login.
     * @param request The request body containing login credentials.
     * @return A Mono emitting the authentication response with a JWT.
     */
    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Endpoint for registering a new customer.
     * @param request The request body containing customer registration details.
     * @return A Mono emitting the authentication response with a JWT.
     */
    @PostMapping("/register/customer")
    public Mono<AuthResponse> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        return authService.registerCustomer(request);
    }
} 