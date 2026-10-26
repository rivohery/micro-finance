--liquibase formatted sql

--changeset alibou:019-create-account-type-interest-rate
CREATE TABLE account_type_interest_rate (
    id uuid NOT NULL PRIMARY KEY,
    account_type_id uuid NOT NULL,
    account_type_code VARCHAR(2) NOT NULL,
    annual_interest_rate numeric(19,4) DEFAULT 0.00 NOT NULL,
    annee VARCHAR(255) NOT NULL
);
