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
@Table("ticket_history")
public class TicketHistory {

    @Id
    @Column("history_id")
    private Long historyId;

    @Column("ticket_id")
    private Long ticketId;

    @Column("previous_pool_id")
    private Long previousPoolId;

    @Column("new_pool_id")
    private Long newPoolId;

    @Column("previous_assigned_to")
    private Long previousAssignedTo;

    @Column("new_assigned_to")
    private Long newAssignedTo;

    @Column("reason")
    private String reason;

    @Column("changed_at")
    private LocalDateTime changedAt;
} 