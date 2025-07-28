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
@Table("pools")
public class Pool {

    @Id
    @Column("pool_id")
    private Long poolId;

    @Column("name")
    private String name;

    @Column("department")
    private String department;
} 