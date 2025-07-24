package com.crm.user.service.impl;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
import com.crm.user.api.dto.response.AuthResponse;
import com.crm.user.data.entity.Customer;
import com.crm.user.data.repository.CustomerRepository;
import com.crm.user.data.repository.UserRepository;
import com.crm.user.security.jwt.JwtUtil;
import com.crm.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Implementation of the AuthService interface.
 * Handles the business logic for user authentication and customer registration.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ReactiveUserDetailsService userDetailsService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        // Use the generic userDetailsService to find the user (can be User or Customer)
        return userDetailsService.findByUsername(request.getEmail())
                .flatMap(userDetails -> {
                    // Check if the provided password matches the stored hashed password
                    if (passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
                        // If passwords match, generate a JWT
                        String token = jwtUtil.generateToken(userDetails);
                        return Mono.just(new AuthResponse(token));
                    } else {
                        // If passwords don't match, return an error
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                })
                // If no user is found with the email, return the same generic error
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }

    @Override
    public Mono<AuthResponse> registerCustomer(RegisterRequest request) {
        Mono<Boolean> userExists = userRepository.findByEmail(request.getEmail()).hasElement();
        Mono<Boolean> customerExists = customerRepository.findByEmail(request.getEmail()).hasElement();

        return Mono.zip(userExists, customerExists)
                .flatMap(tuple -> {
                    boolean anyExists = tuple.getT1() || tuple.getT2();
                    if (anyExists) {
                        return Mono.error(new RuntimeException("Email already in use: " + request.getEmail()));
                    }

                    Customer newCustomer = Customer.builder()
                            .name(request.getName())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .passwordHash(passwordEncoder.encode(request.getPassword()))
                            .role("CUSTOMER")
                            .createdAt(LocalDateTime.now())
                            .build();

                    return customerRepository.save(newCustomer)
                            .map(savedCustomer -> {
                                String token = jwtUtil.generateToken(savedCustomer);
                                return new AuthResponse(token);
                            });
                });
    }
} 