package com.crm.user.service.impl;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
import com.crm.user.api.dto.request.RefreshTokenRequest;
import com.crm.user.api.dto.response.AuthResponse;
import com.crm.user.data.entity.User;
import com.crm.user.data.repository.UserRepository;
import com.crm.user.security.jwt.JwtUtil;
import com.crm.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WebClient departmentWebClient;

    @Value("${app.jwt.refresh-ttl-days}")
    private long refreshTtlDays;

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
                )
                .cast(org.springframework.security.core.Authentication.class)
                .flatMap(auth -> userRepository.findByUserName(request.getUserName()))
                .flatMap(user -> issueTokensForUser(user));
    }

    @Override
    public Mono<AuthResponse> registerUser(RegisterRequest request) {
        return userRepository.findByUserName(request.getUserName())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Kullanıcı adı zaten mevcut: " + request.getUserName()));
                    }
                    Map<String, Object> personPayload = Map.of(
                            "name", request.getName(),
                            "surname", request.getSurname(),
                            "email", request.getEmail(),
                            "phone", request.getPhone(),
                            "departmentId", request.getDepartmentId());
                    return departmentWebClient.post()
                            .uri("/api/persons/internal/register")
                            .bodyValue(personPayload)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(resp -> ((Number) resp.get("personId")).longValue())
                            .flatMap(personId -> {
                                User user = User.builder()
                                        .userName(request.getUserName())
                                        .passwordHash(passwordEncoder.encode(request.getPassword()))
                                        .personId(personId)
                                        .role("ROLE_USER")
                                        .build();
                                return userRepository.save(user).flatMap(this::issueTokensForUser);
                            });
                });
    }

    @Override
    public Mono<AuthResponse> refresh(RefreshTokenRequest request) {
        String presentedRefreshToken = request.getRefreshToken();
        String[] parts = presentedRefreshToken.split("\\.", 2);
        if (parts.length != 2) {
            return Mono.error(new RuntimeException("Invalid refresh token"));
        }
        Long userId;
        try {
            userId = Long.valueOf(parts[0]);
        } catch (NumberFormatException e) {
            return Mono.error(new RuntimeException("Invalid refresh token"));
        }

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")))
                .flatMap(user -> {
                    if (user.getRefreshTokenExpiresAt() == null || user.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
                        return Mono.error(new RuntimeException("Refresh token expired"));
                    }
                    if (user.getRefreshTokenHash() == null || !passwordEncoder.matches(presentedRefreshToken, user.getRefreshTokenHash())) {
                        return Mono.error(new RuntimeException("Invalid refresh token"));
                    }
                    return rotateAndSaveRefreshToken(user).flatMap(this::buildAuthResponse);
                });
    }

    private Mono<AuthResponse> issueTokensForUser(User user) {
        return rotateAndSaveRefreshToken(user).flatMap(this::buildAuthResponse);
    }

    private Mono<UserWithRefresh> rotateAndSaveRefreshToken(User user) {
        String newRefreshToken = generateRefreshTokenForUser(user.getUserId());
        String newRefreshHash = passwordEncoder.encode(newRefreshToken);
        user.setRefreshTokenHash(newRefreshHash);
        user.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(refreshTtlDays));
        return userRepository.save(user).map(saved -> new UserWithRefresh(saved, newRefreshToken));
    }

    private Mono<AuthResponse> buildAuthResponse(UserWithRefresh pair) {
        String accessToken = jwtUtil.generateToken(pair.user());
        String refreshToken = pair.refreshToken();
        return Mono.just(AuthResponse.builder()
                .token(accessToken)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    private String generateRefreshTokenForUser(Long userId) {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        String random = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return userId + "." + random;
    }

    private record UserWithRefresh(User user, String refreshToken) {}

    @Override
    public Mono<Void> logout(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.empty())
                .flatMap(user -> {
                    user.setRefreshTokenHash(null);
                    user.setRefreshTokenExpiresAt(null);
                    return userRepository.save(user).then();
                });
    }
}