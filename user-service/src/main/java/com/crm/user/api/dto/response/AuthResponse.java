package com.crm.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data transfer object for an authentication response.
 * It contains the JWT token returned to the client after a successful login or registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * Backward-compatibility: same as accessToken
     */
    private String token;

    /**
     * Short-lived access token (JWT)
     */
    private String accessToken;

    /**
     * Long-lived opaque refresh token
     */
    private String refreshToken;
} 