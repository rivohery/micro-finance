-- Liquibase formatted sql

-- changeset alibou:add-customers-index
CREATE INDEX idx_customer_first_name ON customers(first_name);
CREATE INDEX idx_customer_last_name ON customers(last_name);
