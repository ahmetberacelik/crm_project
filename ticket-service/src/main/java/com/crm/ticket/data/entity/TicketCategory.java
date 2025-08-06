package com.crm.ticket.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ticket_categories")
public class TicketCategory {

    @Id
    @Column("category_id")
    private Long id;

    @Column("category_key")
    private String categoryKey;

    @Column("display_name")
    private String displayName;

    @Column("target_department_id")
    private Long targetDepartmentId;

    @Column("is_active")
    private Boolean isActive;
}
