-- =============================================================
-- V1: initial schema for department-service
-- =============================================================

-- =====================
-- Departments
-- =====================
CREATE TABLE departments
(
    department_id        BIGINT IDENTITY(1,1) PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    parent_department_id BIGINT NULL,
    isActive             BIT          NOT NULL DEFAULT 1,
    isDeleted            BIT          NOT NULL DEFAULT 0,
    deleted_user_id      BIGINT NULL,
    isUpdated            BIT          NOT NULL DEFAULT 0,
    updated_user_id      BIGINT NULL
);

-- =====================
-- Persons (employee profiles)
-- =====================
CREATE TABLE persons
(
    person_id     BIGINT IDENTITY(1,1) PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    surname       VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    department_id BIGINT       NOT NULL,
    isActive      BIT          NOT NULL DEFAULT 1,
    CONSTRAINT fk_person_department FOREIGN KEY (department_id) REFERENCES departments (department_id)
);
