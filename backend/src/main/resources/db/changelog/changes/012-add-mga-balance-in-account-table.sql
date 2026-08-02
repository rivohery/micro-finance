-- Liquibase formatted sql

-- changeset alibou:012-add-mga-balance-in-account-table
ALTER TABLE accounts ADD COLUMN mga_balance numeric(19,4) DEFAULT 0.00 NOT NULL;
