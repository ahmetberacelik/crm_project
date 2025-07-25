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
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final ReactiveAuthenticationManager authenticationManager;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        ).map(authentication -> {
            String token = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());
            return new AuthResponse(token);
        });
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