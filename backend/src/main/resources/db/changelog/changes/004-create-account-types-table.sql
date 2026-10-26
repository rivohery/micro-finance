--liquibase formatted sql

--changeset alibou:004-create-table-account-types
CREATE TABLE account_types (
    id uuid NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(2) NOT NULL,
    account_fee numeric(19,4) DEFAULT 0.00 NOT NULL,
    interest_rate numeric(19,4) DEFAULT 0.00 NOT NULL,
    minimum_balance numeric(19,4) DEFAULT 0.00 NOT NULL,
    created_date date NOT NULL,
    created_by uuid NOT NULL,
    last_modified_date date,
    last_modified_by uuid,
    -- Constraint d'UNICITÉ
    CONSTRAINT uk_account_types_code UNIQUE (code),
    CONSTRAINT uk_account_types_name UNIQUE (name)
);

