package com.crm.ticket.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignTicketRequest {

    @NotNull
    private Long ticketId;

    @NotNull
    private Long newAssignedTo;

    private String reason;
} 