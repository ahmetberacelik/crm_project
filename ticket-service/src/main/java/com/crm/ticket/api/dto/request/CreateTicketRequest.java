package com.crm.ticket.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotNull
    private Long customerId;

    @NotBlank
    private String issue;

    private String category;

    @Builder.Default
    private String priority = "Orta"; // Düşük, Orta, Yüksek, Acil

    // Optional: initial pool assignment
    private Long poolId;
} 