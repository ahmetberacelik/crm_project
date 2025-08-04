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
@Table("ticket_assignments")
public class TicketAssignment {

    @Id
    @Column("assignment_id")
    private Long id;

    @Column("ticket_id")
    private Long ticketId;

    @Column("is_in_pool")
    private Boolean isInPool;

    @Column("department_id")
    private Long departmentId;

    @Column("person_id")
    private Long personId;

    @Column("assigned_date")
    private LocalDateTime assignedDate;

    @Column("completed_date")
    private LocalDateTime completedDate;

    private String status;
}