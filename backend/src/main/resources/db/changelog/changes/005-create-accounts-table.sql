--liquibase formatted sql

--changeset alibou:create-table-accounts
CREATE TABLE accounts (
    id uuid NOT NULL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL,
    account_status VARCHAR(50) NOT NULL,
    balance numeric(19,4) DEFAULT 0.00 NOT NULL,
    overdraft_limit numeric(19,4) DEFAULT 0.00 NOT NULL,
    customer_id uuid NOT NULL,
    account_type_id uuid NOT NULL,
    currency_id uuid NOT NULL,
    created_date timestamp NOT NULL,
    last_modified_date timestamp
);

--changeset alibou:add-accounts-constraint
ALTER TABLE accounts ADD CONSTRAINT uk_accounts_number UNIQUE (account_number);
ALTER TABLE accounts ADD CONSTRAINT chk_accounts_status CHECK (account_status IN ('ACTIVE', 'PENDING', 'SUSPENDED', 'CLOSED'));
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id);
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_currency_id FOREIGN KEY (currency_id) REFERENCES currencies(id);
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_type_id FOREIGN KEY (account_type_id) REFERENCES account_types(id);