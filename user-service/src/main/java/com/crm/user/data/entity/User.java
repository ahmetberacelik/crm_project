package com.crm.user.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents a record in the 'users' table.
 * This entity is used for internal company staff members like administrators or support agents.
 * Implements UserDetails to integrate with Spring Security.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     * Maps to the 'user_id' primary key column.
     */
    @Id
    @Column("user_id")
    private Long userId;

    /**
     * The full name of the user.
     * Maps to the 'name' column.
     */
    @Column("name")
    private String name;

    /**
     * The user's email address, used for login.
     * Maps to the 'email' column, which is unique.
     */
    @Column("email")
    private String email;

    /**
     * The user's hashed password.
     * Maps to the 'password_hash' column.
     */
    @Column("password_hash")
    private String passwordHash;

    /**
     * The role of the user within the system (e.g., 'ADMIN', 'SUPPORT_AGENT').
     * Maps to the 'role' column.
     */
    @Column("role")
    private String role;

    /**
     * The department the user belongs to (e.g., 'IT', 'Finance').
     * Maps to the 'department' column.
     */
    @Column("department")
    private String department;

    /**
     * The expertise level of the user (e.g., 'Senior', 'Junior').
     * Maps to the 'expertise_level' column.
     */
    @Column("expertise_level")
    private String expertiseLevel;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
} 