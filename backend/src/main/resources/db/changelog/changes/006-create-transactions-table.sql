--liquibase formatted sql

--changeset alibou:006-create-table-transactions
CREATE TABLE transactions (
    id uuid NOT NULL PRIMARY KEY,
    account_id uuid NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    original_amount numeric(19,4) NOT NULL,
    final_amount numeric(19,4) NOT NULL,
    exchange_rate numeric(19,4) NOT NULL,
    operator_name VARCHAR(50) NOT NULL,
    reference VARCHAR(50) NOT NULL,
    transaction_currency VARCHAR(10) NOT NULL,
    target_currency VARCHAR(10) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    created_date timestamp NOT NULL
);

--changeset alibou:006-add-transactions-constraint
ALTER TABLE transactions ADD CONSTRAINT chk_transactions_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFERT'));
ALTER TABLE transactions ADD CONSTRAINT uk_transactions_ref UNIQUE (reference);
ALTER TABLE transactions ADD CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id) REFERENCES accounts(id);