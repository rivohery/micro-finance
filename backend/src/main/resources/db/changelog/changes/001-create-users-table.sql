--liquibase formatted sql

--changeset alibou:create-table-users
CREATE TABLE users (
    id uuid NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    enable boolean NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL
);

--changeset alibou:add-users-constraint
ALTER TABLE users ADD CONSTRAINT uk_user_email UNIQUE (email);
ALTER TABLE users ADD CONSTRAINT uk_user_username UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT chk_user_role CHECK (role IN ('EMPLOYE', 'ADMIN', 'CLIENT'));