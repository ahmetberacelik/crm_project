-- =================================================================
-- V1: Initial Schema (Refactored)
-- Açıklama: Kullanıcı, departman, personel, müşteri ve ticket
-- tablolarını (ticket_assignments dahil) oluşturur.
-- =================================================================

-- =====================
-- Departmanlar
-- =====================
CREATE TABLE departments (
    department_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_department_id BIGINT NULL,
    isActive BIT NOT NULL DEFAULT 1,
    isDeleted BIT NOT NULL DEFAULT 0,
    deleted_user_id BIGINT NULL,
    isUpdated BIT NOT NULL DEFAULT 0,
    updated_user_id BIGINT NULL,
    CONSTRAINT fk_department_parent FOREIGN KEY(parent_department_id)
        REFERENCES departments(department_id)
);

-- =====================
-- Person (Çalışan Profili)
-- =====================
CREATE TABLE persons (
    person_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    department_id BIGINT NOT NULL,
    isActive BIT NOT NULL DEFAULT 1,
    CONSTRAINT fk_person_department FOREIGN KEY(department_id)
        REFERENCES departments(department_id)
);

-- =====================
-- Kullanıcı Kimliği
-- =====================
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    person_id BIGINT,
    refresh_token_hash VARCHAR(255),
    refresh_token_expires_at DATETIME2,
    email VARCHAR(255),
    phone VARCHAR(20),
    CONSTRAINT fk_user_person FOREIGN KEY(person_id)
        REFERENCES persons(person_id)
);

-- =====================
-- Müşteriler
-- =====================
CREATE TABLE customers (
    customer_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    surname VARCHAR(255),
    phone VARCHAR(20)
);

-- =====================
-- Ticketlar
-- =====================
CREATE TABLE tickets (
    ticket_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    isActive BIT NOT NULL DEFAULT 1,
    created_date DATETIME2 DEFAULT CURRENT_TIMESTAMP,
    closed_date DATETIME2 NULL,
    isEmployee BIT NOT NULL,
    creater_id BIGINT NOT NULL -- user_id veya customer_id
);

-- =====================
-- Ticket Atamaları
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
    CONSTRAINT fk_assignment_ticket FOREIGN KEY(ticket_id) REFERENCES tickets(ticket_id),
    CONSTRAINT fk_assignment_department FOREIGN KEY(department_id) REFERENCES departments(department_id),
    CONSTRAINT fk_assignment_person FOREIGN KEY(person_id) REFERENCES persons(person_id)
); 