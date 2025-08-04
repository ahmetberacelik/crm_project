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
@Table("tickets")
public class Ticket {

    @Id
    @Column("ticket_id")
    private Long id;

    private String issue;

    private String priority;

    @Column("is_active")
    private Boolean isActive;

    @Column("created_date")
    private LocalDateTime createdDate;

    @Column("closed_date")
    private LocalDateTime closedDate;

    @Column("is_employee")
    private Boolean isEmployee;

    @Column("creator_id")
    private Long creatorId;
}