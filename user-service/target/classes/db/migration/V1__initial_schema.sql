-- =================================================================
-- V1: Initial Schema
-- Description: Creates the initial tables for 'users' and 'customers'
-- which are required for the user-service authentication.
-- =================================================================

-- Create the 'users' table for internal company staff (Admins, Support Agents, etc.)
CREATE TABLE users (
    user_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    expertise_level VARCHAR(50)
);

-- Create the 'customers' table for external clients who create tickets.
CREATE TABLE customers (
    customer_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    created_at DATETIME2 DEFAULT CURRENT_TIMESTAMP
);

-- Note: Other tables like 'pools', 'support_tickets', 'ticket_history', 'notifications'
-- will be created in separate migration files, likely belonging to a 'ticket-service'
-- or a shared module in the future, to maintain service boundaries. 