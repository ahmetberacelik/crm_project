package com.crm.ticket.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTicketEmployeeRequest {
    @NotBlank
    @Size(min = 10, max = 4000)
    private String issue;

    @NotBlank
    private String priority;

    @NotBlank
    private String category;
}
