package com.crm.user.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data transfer object for a registration request.
 * It contains all possible fields for registering either a User or a Customer.
 * Specific fields will be used depending on the registration type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * The full name of the person registering.
     */
    private String name;

    /**
     * The email address, which will be used for login.
     */
    private String email;

    /**
     * The plain-text password. It will be hashed before being stored.
     */
    private String password;

    /**
     * The phone number (primarily for Customers).
     */
    private String phone;

    /**
     * The department the user belongs to (only for Users).
     */
    private String department;

    /**
     * The expertise level of the user (only for Users).
     */
    private String expertiseLevel;
} 