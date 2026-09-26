-- Liquibase formatted sql

-- changeset alibou:017-add-column-version-in-accounts-table
ALTER TABLE accounts ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;
