package com.crm.user.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

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
    @NotBlank(message = "Name cannot be blank")
    private String name;

    /**
     * The surname of the person registering.
     */
    @NotBlank(message = "Surname cannot be blank")
    private String surname;

    /**
     * Email address (unique).
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Kullanıcı adı (benzersiz).
     */
    @NotBlank(message = "User name cannot be blank")
    private String userName;



    /**
     * The plain-text password. It will be hashed before being stored.
     */
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * The phone number (primarily for Customers).
     */
    private String phone;

    /**
     * Kullanıcının bağlı olduğu departman adı.
     */
    @NotBlank(message = "Department cannot be blank")
    private String department;

} 