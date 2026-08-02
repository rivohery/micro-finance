-- Liquibase formatted sql

-- changeset alibou:017-add-new-column-of-interest-rate-trace-table
ALTER TABLE interest_rate_trace ADD COLUMN interest_rate numeric(19,4) DEFAULT 0.00 NOT NULL;
ALTER TABLE interest_rate_trace ADD COLUMN currency_code varchar(10) NOT NULL;