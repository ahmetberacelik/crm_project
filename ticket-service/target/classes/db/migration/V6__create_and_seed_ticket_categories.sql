-- Create the table to manage ticket category to department routing
CREATE TABLE ticket_categories (
    category_id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_key NVARCHAR(100) NOT NULL UNIQUE, -- Key used by the API (e.g., "TECHNICAL_SUPPORT")
    display_name NVARCHAR(255) NOT NULL,       -- Name shown to the user in the UI
    target_department_id BIGINT NOT NULL,       -- The ID of the department in the department-service
    is_active BIT NOT NULL DEFAULT 1
);

-- Seed the table with initial routing rules based on the existing department structure
INSERT INTO ticket_categories (category_key, display_name, target_department_id) VALUES
('TECHNICAL_SUPPORT', 'Teknik Destek / Donanım Sorunu', 14),
('NETWORK_ISSUE', 'Ağ / Sistem Erişimi / Şifre Sorunu', 15),
('SECURITY_CONCERN', 'Güvenlik Endişesi / Phishing Bildirimi', 16),
('BILLING', 'Fatura / Ödeme Sorunu', 18),
('PURCHASE_REQUEST', 'Satın Alma Talebi', 19),
('LEAVE_REQUEST', 'İzin / Bordro Sorusu', 22),
('TRAINING_REQUEST', 'Eğitim ve Gelişim Talebi', 23),
('LEGAL_CONSULTATION', 'Hukuki Danışmanlık', 25),
('MEDIA_INQUIRY', 'Medya ve Halkla İlişkiler', 3);