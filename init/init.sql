CREATE ROLE account WITH LOGIN PASSWORD 'account';
CREATE DATABASE accounts OWNER account;
GRANT ALL PRIVILEGES ON DATABASE accounts TO account;

\connect accounts postgres

CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_number VARCHAR(255) UNIQUE NOT NULL,
    account_type VARCHAR(255) NOT NULL CHECK (account_type IN ('SALARY', 'CURRENT', 'SAVINGS', 'NRE')),
    balance NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(255) NOT NULL CHECK (currency IN ('INR', 'USD', 'EUR')),
    status VARCHAR(255) NOT NULL CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    created_at TIMESTAMP NOT NULL
);

-- read-optimized projection from customer service
CREATE TABLE IF NOT EXISTS customer_projection (
    customer_id INT PRIMARY KEY,
    name VARCHAR(255)
);

COPY accounts(customer_id, account_number, account_type, balance, currency, status, created_at)
FROM '/docker-entrypoint-initdb.d/data/accounts.csv'
WITH (FORMAT csv, HEADER true);

ALTER TABLE accounts OWNER TO account;
ALTER TABLE customer_projection OWNER TO account;
