CREATE TABLE accounts (
    account_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_number VARCHAR(255) UNIQUE NOT NULL,
    account_type VARCHAR(255) NOT NULL CHECK (account_type IN ('SALARY', 'CURRENT', 'SAVINGS', 'NRE')),
    balance NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(255) NOT NULL CHECK (currency IN ('INR', 'USD', 'EUR')),
    status VARCHAR(255) NOT NULL CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    created_at TIMESTAMP NOT NULL
);
