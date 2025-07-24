package com.crm.user.api.controller;

import com.crm.user.api.dto.response.UserInfoResponse;
import com.crm.user.data.entity.Customer;
import com.crm.user.data.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

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
} 