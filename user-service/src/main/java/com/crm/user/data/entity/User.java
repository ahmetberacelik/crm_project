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
     * Kullanıcının giriş için kullandığı kullanıcı adı.
     * 'user_name' sütununa map edilir ve benzersizdir.
     */
    @Column("user_name")
    private String userName;

    /**
     * BCrypt ile hashlenmiş şifre.
     */
    @Column("password_hash")
    private String passwordHash;

    /**
     * Person (çalışan profili) tablosuna bağlanan yabancı anahtar.
     */
    @Column("person_id")
    private Long personId;

    @Column("role")
    private String role;

    /**
     * Kullanıcının tek aktif refresh-token hash değeri.
     */
    @Column("refresh_token_hash")
    private String refreshTokenHash;

    /**
     * Refresh-token süresinin sona ereceği zaman.
     */
    @Column("refresh_token_expires_at")
    private java.time.LocalDateTime refreshTokenExpiresAt;

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
        return userName;
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