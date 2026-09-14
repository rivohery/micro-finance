-- Liquibase formatted sql

-- changeset alibou:013-add-sold-before-transaction-in-transactions-table
ALTER TABLE transactions ADD COLUMN sold_before_transaction numeric(19,4) DEFAULT 0.00 NOT NULL;
