package com.crm.ticket.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReassignTicketRequest {

    @NotNull(message = "New department ID cannot be null")
    private Long newDepartmentId;
}