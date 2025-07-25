-- =================================================================
-- V2: Insert Default Admin User
-- Description: Inserts a default administrator user into the 'users' table.
-- This user can be used for initial setup and system management.
-- =================================================================

-- The password is 'adminpassword', pre-hashed using BCrypt.
-- It's crucial to change this password after the first login for security.
INSERT INTO users (name, email, password_hash, role, department, expertise_level)
VALUES (
    'Admin User',
    'admin@crm.com',
    '$2a$10$lacemngh.LuREz6P0QiEAODTn.Git17N49fI28sxZHrKRakHw0wTa', -- Hashed 'adminpassword'
    'ADMIN',
    'IT',
    'Senior'
); 