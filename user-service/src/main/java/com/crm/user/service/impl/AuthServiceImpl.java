package com.crm.user.service.impl;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WebClient departmentWebClient;

    @Override
    public Mono<AuthResponse> login(LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        ).map(auth -> AuthResponse.builder().token(jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal())).build());
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
                                        .role("ROLE_USER") // Assign default role
                                        .build();
                                return userRepository.save(user)
                                        .map(saved -> AuthResponse.builder().token(jwtUtil.generateToken(saved)).build());
                            });
                });
    }
}