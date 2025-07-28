-- Ticket service schema

CREATE TABLE pools (
    pool_id BIGINT IDENTITY PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    department NVARCHAR(50) NOT NULL
);

CREATE TABLE support_tickets (
    ticket_id BIGINT IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    issue NVARCHAR(MAX) NOT NULL,
    category NVARCHAR(100),
    priority NVARCHAR(50) DEFAULT 'Orta',
    status NVARCHAR(50) DEFAULT 'Açık',
    pool_id BIGINT,
    assigned_to BIGINT,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE ticket_history (
    history_id BIGINT IDENTITY PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    previous_pool_id BIGINT,
    new_pool_id BIGINT,
    previous_assigned_to BIGINT,
    new_assigned_to BIGINT,
    reason NVARCHAR(MAX),
    changed_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE notifications (
    notification_id BIGINT IDENTITY PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    sent_to NVARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT GETDATE()
); 