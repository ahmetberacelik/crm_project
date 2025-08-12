package com.crm.user.service;

import com.crm.user.api.dto.request.LoginRequest;
import com.crm.user.api.dto.request.RegisterRequest;
import com.crm.user.api.dto.request.RefreshTokenRequest;
import com.crm.user.api.dto.response.AuthResponse;
import reactor.core.publisher.Mono;

/**
 * Kimlik doğrulama işlemlerini yöneten servis arabirimi.
 */
public interface AuthService {

    /**
     * Kullanıcı girişi.
     * @param request kullanıcı adı + şifre
     * @return JWT içeren AuthResponse
     */
    Mono<AuthResponse> login(LoginRequest request);

    /**
     * Yeni kullanıcı kaydı.
     * @param request kayıt bilgileri
     * @return JWT içeren AuthResponse
     */
    Mono<AuthResponse> registerUser(RegisterRequest request);

    /**
     * Refresh access token using a valid refresh token
     */
    Mono<AuthResponse> refresh(RefreshTokenRequest request);
}
