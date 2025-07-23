package com.crm.user.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Represents a record in the 'customers' table.
 * This entity is used for external clients who register and create support tickets.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("customers")
public class Customer {

    /**
     * The unique identifier for the customer.
     * Maps to the 'customer_id' primary key column.
     */
    @Id
    @Column("customer_id")
    private Long customerId;

    /**
     * The full name of the customer.
     * Maps to the 'name' column.
     */
    @Column("name")
    private String name;

    /**
     * The customer's email address, used for login.
     * Maps to the 'email' column, which is unique.
     */
    @Column("email")
    private String email;

    /**
     * The customer's phone number.
     * Maps to the 'phone' column.
     */
    @Column("phone")
    private String phone;

    /**
     * The customer's hashed password.
     * Maps to the 'password_hash' column.
     */
    @Column("password_hash")
    private String passwordHash;

    /**
     * The role of the user, which is 'CUSTOMER' by default.
     * Maps to the 'role' column.
     */
    @Column("role")
    private String role;

    /**
     * The timestamp when the customer record was created.
     * Maps to the 'created_at' column.
     */
    @Column("created_at")
    private LocalDateTime createdAt;
} 