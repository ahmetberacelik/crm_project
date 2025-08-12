-- Create the table to manage ticket category to department routing
CREATE TABLE ticket_categories
(
    category_id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_key         NVARCHAR(100) NOT NULL UNIQUE, -- Key used by the API (e.g., "TECHNICAL_SUPPORT")
    display_name         NVARCHAR(255) NOT NULL,        -- Name shown to the user in the UI
    target_department_id BIGINT NOT NULL,               -- The ID of the department in the department-service
    is_active            BIT    NOT NULL DEFAULT 1
);

-- Seed the table with initial routing rules based on the existing department structure
INSERT INTO ticket_categories (category_key, display_name, target_department_id)
VALUES ('GENERAL_MANAGEMENT', 'Genel Müdürlük', 0),
       ('CORPORATE_COMMUNICATIONS_MARKETING', 'Kurumsal İletişim ve Pazarlama', 1),
       ('EVENT_ORGANIZATION', 'Etkinlik ve Organizasyon', 2),
       ('MEDIA_INQUIRY', 'Medya ve Halkla İlişkiler', 3),
       ('DIGITAL_MARKETING_SOCIAL_MEDIA', 'Dijital Pazarlama ve Sosyal Medya', 4),
       ('BUSINESS_DEVELOPMENT_ENTREPRENEURSHIP', 'İş Geliştirme ve Girişimcilik', 5),
       ('ENTREPRENEURSHIP_SUPPORT', 'Girişimcilik Destek', 6),
       ('INVESTOR_RELATIONS', 'Yatırımcı İlişkileri', 7),
       ('PROJECT_COLLABORATIONS', 'Proje ve İş Birlikleri', 8),
       ('TECHNOLOGY_TRANSFER_COMMERCIALIZATION', 'Teknoloji Transfer ve Ticarileştirme', 9),
       ('PATENT_INTELLECTUAL_PROPERTY', 'Patent ve Fikri Sınai Haklar', 10),
       ('UNIVERSITY_INDUSTRY_COOPERATION', 'Üniversite-Sanayi İşbirliği', 11),
       ('COMMERCIALIZATION_LICENSING', 'Ticarileştirme ve Lisanslama', 12),
       ('IT_INFRASTRUCTURE_SERVICES', 'Bilişim ve Altyapı Hizmetleri', 13),
       ('TECHNICAL_SUPPORT', 'Teknik Destek / Donanım Sorunu', 14),
       ('NETWORK_ISSUE', 'Ağ / Sistem Erişimi / Şifre Sorunu', 15),
       ('SECURITY_CONCERN', 'Güvenlik Endişesi / Phishing Bildirimi', 16),
       ('FINANCIAL_ADMINISTRATIVE_AFFAIRS', 'Mali ve İdari İşler', 17),
       ('BILLING', 'Fatura / Ödeme Sorunu', 18),
       ('PURCHASE_REQUEST', 'Satın Alma Talebi', 19),
       ('BUDGET_PLANNING_REPORTING', 'Bütçe Planlama ve Raporlama', 20),
       ('HUMAN_RESOURCES', 'İnsan Kaynakları', 21),
       ('LEAVE_REQUEST', 'İzin / Bordro Sorusu', 22),
       ('TRAINING_REQUEST', 'Eğitim ve Gelişim Talebi', 23),
       ('PERFORMANCE_CAREER_MANAGEMENT', 'Performans ve Kariyer Yönetimi', 24),
       ('LEGAL_CONSULTATION', 'Hukuki Danışmanlık', 25),
       ('INTERNAL_AUDIT_QUALITY_MANAGEMENT', 'İç Denetim ve Kalite Yönetimi', 26);