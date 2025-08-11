package com.crm.department.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents a record in the 'departments' table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("departments")
public class Department {

    @Id
    @Column("department_id")
    private Long departmentId;

    @Column("name")
    private String name;

    @Column("parent_department_id")
    private Long parentDepartmentId;

    // Note: Migration uses camelCase column names such as 'isActive'.
    @Column("isActive")
    private Boolean isActive;

    @Column("isDeleted")
    private Boolean isDeleted;

    @Column("deleted_user_id")
    private Long deletedUserId;

    @Column("isUpdated")
    private Boolean isUpdated;

    @Column("updated_user_id")
    private Long updatedUserId;
}


