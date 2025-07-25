package com.crm.user.api.controller;

import com.crm.user.api.dto.response.UserInfoResponse;
import com.crm.user.data.entity.Customer;
import com.crm.user.data.entity.User;
import com.crm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Mono<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return Mono.just(UserInfoResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build());
        } else if (userDetails instanceof Customer customer) {
            return Mono.just(UserInfoResponse.builder()
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .role(customer.getRole())
                    .build());
        } else {
            return Mono.empty(); // Or handle as an error
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Flux<UserInfoResponse> getAllUsers() {
        return userService.findAll()
                .map(user -> UserInfoResponse.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build());
    }
} 