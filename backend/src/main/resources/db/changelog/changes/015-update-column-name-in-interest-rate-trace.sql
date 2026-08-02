-- Liquibase formatted sql

-- changeset alibou:015-update-column-name-in-interest-rate-trace
ALTER TABLE interest_rate_trace RENAME column accountnumber to account_number;