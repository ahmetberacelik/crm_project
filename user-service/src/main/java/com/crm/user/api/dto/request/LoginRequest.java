package com.crm.user.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the data transfer object for a login request.
 * It contains the credentials required for a user or customer to authenticate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Kullanıcı adı.
     */
    @NotBlank(message = "User name cannot be blank")
    private String userName;

    /**
     * The plain-text password for authentication.
     */
    @NotBlank(message = "Password cannot be blank")
    private String password;
} 