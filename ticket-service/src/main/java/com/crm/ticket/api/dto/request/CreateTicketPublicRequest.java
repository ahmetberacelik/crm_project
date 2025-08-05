package com.crm.ticket.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTicketPublicRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 2, max = 100)
    private String surname;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotBlank
    @Size(min = 10, max = 4000)
    private String issue;

    @NotBlank
    private String priority; // Örn: "MEDIUM"

    @NotBlank
    private String category; // Kural motoru için, örn: "TECHNICAL_SUPPORT"
}
