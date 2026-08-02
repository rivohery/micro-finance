--liquibase formatted sql

--changeset alibou:create-table-account-status-histories
CREATE TABLE account_status_histories (
    id uuid NOT NULL PRIMARY KEY,
    account_id uuid NOT NULL,
    reason VARCHAR(255) NOT NULL,
    old_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    doing_by VARCHAR(50) NOT NULL,
    doing_at timestamp NOT NULL
);

--changeset alibou:add-account-status-histories-constraint
ALTER TABLE account_status_histories ADD CONSTRAINT chk_accounts_old_status CHECK (old_status IN ('ACTIVE', 'PENDING', 'SUSPENDED', 'CLOSED'));
ALTER TABLE account_status_histories ADD CONSTRAINT chk_accounts_new_status CHECK (new_status IN ('ACTIVE', 'PENDING', 'SUSPENDED', 'CLOSED'));
ALTER TABLE account_status_histories ADD CONSTRAINT fk_account_status_history_account_id FOREIGN KEY (account_id) REFERENCES accounts(id);