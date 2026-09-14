-- Liquibase formatted sql

-- changeset alibou:022-put-default-value-createdBy-in-accounts-table
ALTER TABLE accounts ALTER COLUMN created_by SET DEFAULT '00000000-0000-0000-0000-000000000000';
