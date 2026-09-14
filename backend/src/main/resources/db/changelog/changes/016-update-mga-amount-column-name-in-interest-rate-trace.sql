-- Liquibase formatted sql

-- changeset alibou:016-update-mga-amount-column-name-in-interest-rate-trace
ALTER TABLE interest_rate_trace RENAME column mgaamount to mga_amount;