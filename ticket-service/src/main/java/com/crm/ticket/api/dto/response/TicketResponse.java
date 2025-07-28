package com.crm.ticket.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long ticketId;
    private Long customerId;
    private String issue;
    private String category;
    private String priority;
    private String status;
    private Long poolId;
    private Long assignedTo;
    private LocalDateTime createdAt;
} 