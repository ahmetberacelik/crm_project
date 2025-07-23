package com.crm.user.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * The email address of the user or customer.
     */
    private String email;

    /**
     * The plain-text password for authentication.
     */
    private String password;
} 