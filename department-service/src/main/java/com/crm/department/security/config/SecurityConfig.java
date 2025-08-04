package com.crm.department.security.config;

import com.crm.department.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/internal/**").permitAll() // Allow internal service-to-service communication
                        
                        // Departments API
                        .pathMatchers(HttpMethod.GET, "/api/departments/**").authenticated()
                        .pathMatchers(HttpMethod.POST, "/api/departments").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/departments/**").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/departments/**").hasAuthority("ROLE_ADMIN")
                        
                        // Persons API
                        .pathMatchers("/api/persons/internal/**").permitAll() // Allow registration flow
                        .pathMatchers(HttpMethod.GET, "/api/persons/all").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/persons/inactive").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/persons/{id}/reactivate").hasAuthority("ROLE_ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/persons").hasAuthority("ROLE_ADMIN") // Only admins can create new persons directly
                        .pathMatchers("/api/persons/**").authenticated()

                        // Deny any other unauthenticated access
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
