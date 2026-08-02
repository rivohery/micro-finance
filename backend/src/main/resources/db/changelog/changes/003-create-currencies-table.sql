--liquibase formatted sql

--changeset alibou:create-table-currencies
CREATE TABLE currencies (
    id uuid NOT NULL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    enable boolean NOT NULL,
    -- Contrainte d'UNICITÉ
    CONSTRAINT uk_currencies_code UNIQUE (code),
    CONSTRAINT uk_currencies_name UNIQUE (name)
);
