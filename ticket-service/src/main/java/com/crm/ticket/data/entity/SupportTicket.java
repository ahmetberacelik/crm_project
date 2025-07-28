package com.crm.ticket.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("support_tickets")
public class SupportTicket {

    @Id
    @Column("ticket_id")
    private Long ticketId;

    @Column("customer_id")
    private Long customerId;

    @Column("issue")
    private String issue;

    @Column("category")
    private String category;

    @Column("priority")
    private String priority;

    @Column("status")
    private String status;

    @Column("pool_id")
    private Long poolId;

    @Column("assigned_to")
    private Long assignedTo;

    @Column("created_at")
    private LocalDateTime createdAt;
} 