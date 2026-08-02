-- Liquibase formatted sql

-- changeset alibou:014-create-interest-rate-trace-table
CREATE TABLE interest_rate_trace (
    id uuid NOT NULL PRIMARY KEY,
    accountNumber VARCHAR(255) NOT NULL,
    amount numeric(19,4) DEFAULT 0.00 NOT NULL,
    mgaAmount numeric(19,4) DEFAULT 0.00 NOT NULL,
    col_month VARCHAR(50) NOT NULL,
    col_year VARCHAR(10) NOT NULL
);

-- changeset alibou:add-interest-rate-trace-index
CREATE INDEX dx_col_month ON interest_rate_trace(col_month);