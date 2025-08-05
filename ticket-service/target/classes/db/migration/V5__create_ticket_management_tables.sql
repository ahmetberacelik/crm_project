-- Create the customers table to store external customer information
CREATE TABLE customers (
    customer_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    surname NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    phone NVARCHAR(50)
);

-- Create the main tickets table
CREATE TABLE tickets (
    ticket_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    issue NVARCHAR(MAX) NOT NULL,
    priority NVARCHAR(50) NOT NULL,
    is_active BIT NOT NULL,
    created_date DATETIME2 NOT NULL,
    closed_date DATETIME2,
    is_employee BIT NOT NULL,
    creator_id BIGINT NOT NULL
);

-- Create the ticket_assignments table to track the lifecycle and ownership of a ticket
CREATE TABLE ticket_assignments (
    assignment_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    is_in_pool BIT NOT NULL,
    department_id BIGINT,
    person_id BIGINT,
    assigned_date DATETIME2 NOT NULL,
    completed_date DATETIME2,
    status NVARCHAR(50) NOT NULL,
    CONSTRAINT fk_ticket_assignments_ticket_id FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id)
);

-- Create an index on ticket_id in the assignments table for faster lookups
CREATE INDEX idx_ticket_assignments_ticket_id ON ticket_assignments(ticket_id);

-- Create an index on department_id and person_id for faster querying of assignments
CREATE INDEX idx_ticket_assignments_department_id ON ticket_assignments(department_id);
CREATE INDEX idx_ticket_assignments_person_id ON ticket_assignments(person_id);