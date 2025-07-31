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

-- =====================
-- Customers (optional, kept for ticket-service FK references)
-- =====================
CREATE TABLE customers (
    customer_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    surname VARCHAR(255),
    phone VARCHAR(20)
);

-- =====================
-- Tickets
-- =====================
CREATE TABLE tickets (
    ticket_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    isActive BIT NOT NULL DEFAULT 1,
    created_date DATETIME2 DEFAULT CURRENT_TIMESTAMP,
    closed_date DATETIME2 NULL,
    isEmployee BIT NOT NULL,
    creater_id BIGINT NOT NULL
);

-- =====================
-- Ticket Assignments
-- =====================
CREATE TABLE ticket_assignments (
    assignment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    is_in_pool BIT NOT NULL,
    department_id BIGINT NOT NULL,
    person_id BIGINT NULL,
    assignedDate DATETIME2 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completedDate DATETIME2 NULL,
    CONSTRAINT fk_assignment_ticket FOREIGN KEY(ticket_id) REFERENCES tickets(ticket_id)
);
