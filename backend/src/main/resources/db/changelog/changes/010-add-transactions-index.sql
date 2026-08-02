-- Liquibase formatted sql

-- changeset alibou:add-transactions-created-date-index
CREATE INDEX idx_transactions_created_date ON transactions(created_date);
