package com.crm.user.api.controller;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
import com.crm.user.api.dto.request.RefreshTokenRequest;
import com.crm.user.api.dto.response.AuthResponse;
import com.crm.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/refresh")
    public Mono<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> logout(@AuthenticationPrincipal String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        return authService.logout(userId);
    }
}
