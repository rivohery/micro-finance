-- Liquibase formatted sql

-- changeset alibou:add-accounts-customer-id-index
CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);
