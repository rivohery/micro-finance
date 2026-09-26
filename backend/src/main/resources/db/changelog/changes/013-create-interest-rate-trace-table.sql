-- Liquibase formatted sql

-- changeset alibou:013-create-interest-rate-trace-table
CREATE TABLE interest_rate_trace (
    id uuid NOT NULL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL,
    amount numeric(19,4) DEFAULT 0.00 NOT NULL,
    mga_exchange_rate numeric(19,4) DEFAULT 0.00 NOT NULL,
    mga_amount numeric(19,4) DEFAULT 0.00 NOT NULL,
    col_month VARCHAR(50) NOT NULL,
    col_year VARCHAR(10) NOT NULL
);

-- changeset alibou:013-add-interest-rate-trace-index
CREATE INDEX idx_col_month ON interest_rate_trace(col_month);