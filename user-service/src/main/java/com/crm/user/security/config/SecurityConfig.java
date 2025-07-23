package com.crm.user.security.config;

import com.crm.user.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.SessionManagementSpec;


/**
 * Main configuration class for Spring Security.
 * This class will hold beans and settings related to authentication and authorization.
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Provides a PasswordEncoder bean to the Spring application context.
     * This bean is used for encoding and validating passwords securely.
     * We are using BCrypt, which is a strong hashing algorithm that includes a salt.
     *
     * @return an instance of BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the main security filter chain for the application.
     * This is the central place to define security rules.
     *
     * @param http the {@link ServerHttpSecurity} to configure.
     * @return the configured {@link SecurityWebFilterChain}.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF protection as we are using JWTs and not browser sessions
                .csrf(CsrfSpec::disable)

                // Configure authorization rules for endpoints
                .authorizeExchange(authorize -> authorize
                        // Allow unauthenticated access to authentication-related endpoints
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        // Any other request must be authenticated
                        .anyExchange().authenticated()
                )

                // Add our custom JWT authentication filter before the standard authentication filter
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                
                // We don't want to create sessions, JWT makes our auth stateless
                .sessionManagement(SessionManagementSpec::disable)
                
                .build();
    }
} 