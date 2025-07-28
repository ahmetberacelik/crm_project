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
@Table("notifications")
public class Notification {

    @Id
    @Column("notification_id")
    private Long notificationId;

    @Column("ticket_id")
    private Long ticketId;

    @Column("message")
    private String message;

    @Column("sent_to")
    private String sentTo;

    @Column("created_at")
    private LocalDateTime createdAt;
} 