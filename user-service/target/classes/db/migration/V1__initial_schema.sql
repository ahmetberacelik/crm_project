-- =============================================================
-- V1: initial schema for user-service (only user-domain tables)
-- =============================================================

-- =====================
-- Users
-- =====================
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    person_id BIGINT NULL,
    refresh_token_hash VARCHAR(255),
    refresh_token_expires_at DATETIME2
);
