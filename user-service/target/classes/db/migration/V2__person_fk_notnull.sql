-- Set person_id column to NOT NULL now that department-service supplies it
ALTER TABLE users
ALTER COLUMN person_id BIGINT NOT NULL;
