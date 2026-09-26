--liquibase formatted sql

--changeset alibou:005-create-table-accounts
CREATE TABLE accounts (
    id uuid NOT NULL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL,
    account_status VARCHAR(50) NOT NULL,
    balance numeric(19,4) DEFAULT 0.00 NOT NULL,
    overdraft_limit numeric(19,4) DEFAULT 0.00 NOT NULL,
    customer_id uuid NOT NULL,
    account_type_id uuid NOT NULL,
    account_type_name VARCHAR(50) NOT NULL,
    account_type_code VARCHAR(2) NOT NULL,
    currency_id uuid NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    annual_interest_rate numeric(19,4) DEFAULT 0.00 NOT NULL,
    created_date date NOT NULL,
    created_by uuid NOT NULL,
    last_modified_date date,
    last_modified_by uuid
);

--changeset alibou:005-add-accounts-constraint
ALTER TABLE accounts ADD CONSTRAINT uk_accounts_number UNIQUE (account_number);
ALTER TABLE accounts ADD CONSTRAINT chk_accounts_status CHECK (account_status IN ('ACTIVE', 'PENDING', 'SUSPENDED', 'CLOSED'));
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id);
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_currency_id FOREIGN KEY (currency_id) REFERENCES currencies(id);
ALTER TABLE accounts ADD CONSTRAINT fk_accounts_type_id FOREIGN KEY (account_type_id) REFERENCES account_types(id);