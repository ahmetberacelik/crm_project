package com.crm.department.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a record in the 'persons' table (employee profile).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("persons")
public class Person {

    @Id
    @Column("person_id")
    private Long personId;

    private String name;

    private String surname;

    private String email;

    private String phone;

    @Column("department_id")
    private Long departmentId;

    @Column("isActive")
    private Boolean isActive;
}


