-- Check if the database 'crm_db' already exists.
-- If it does not exist, create a new database.
IF NOT EXISTS (SELECT 1 FROM sys.databases WHERE name = 'crm_db')
BEGIN
    CREATE DATABASE crm_db;
    PRINT 'Database crm_db created successfully.';
END
ELSE
BEGIN
    PRINT 'Database crm_db already exists.';
END
GO 