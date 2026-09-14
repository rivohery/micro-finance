--liquibase formatted sql

--changeset alibou:create-table-customer
CREATE TABLE customers (
    id uuid NOT NULL PRIMARY KEY,
    created_by uuid NOT NULL,
    created_date date NOT NULL,
    last_modified_by uuid,
    last_modified_date date,
    address_city VARCHAR(50) NOT NULL,
    address_country VARCHAR(50) NOT NULL,
    address_value VARCHAR(255) NOT NULL,
    address_zip_code VARCHAR(10),
    cin VARCHAR(50) NOT NULL,
    date_of_birth date NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    occupation VARCHAR(50) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    user_id uuid NOT NULL
);

--changeset alibou:add-customer-constraint
ALTER TABLE customers ADD CONSTRAINT uk_customer_email UNIQUE (email);
ALTER TABLE customers ADD CONSTRAINT uk_customer_cin UNIQUE (cin);
ALTER TABLE customers ADD CONSTRAINT uk_customer_phone_number UNIQUE (phone_number);
ALTER TABLE customers ADD CONSTRAINT chk_customer_status CHECK (status IN ('ACTIVE', 'PENDING', 'SUSPENDED', 'CLOSED'));
ALTER TABLE customers ADD CONSTRAINT uk_customers_user_id UNIQUE (user_id);
ALTER TABLE customers ADD CONSTRAINT fk_customers_user_id FOREIGN KEY (user_id) REFERENCES users(id);