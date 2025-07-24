package com.crm.user.security.config;

import com.crm.user.data.repository.CustomerRepository;
import com.crm.user.data.repository.UserRepository;
import com.crm.user.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

/**
 * Main configuration class for Spring Security.
 * This class will hold beans and settings related to authentication and authorization.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public SecurityConfig(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return username -> userRepository.findByEmail(username)
                .map(UserDetails.class::cast)
                .switchIfEmpty(customerRepository.findByEmail(username)
                        .map(UserDetails.class::cast))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with email: " + username)));
    }


    @Bean
    @Order(1)
    public SecurityWebFilterChain publicApiFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/auth/**"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(authorize -> authorize.anyExchange().permitAll())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain privateApiFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        return http
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/**"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(authorize -> authorize.anyExchange().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
} 