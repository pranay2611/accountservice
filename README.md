# Account Service
Banking Account Service for managing accounts.

## Author & Repository
### Name
Pranay Kumar Biswas

### BITS ID
2024TM93042

### GitHub repository
https://github.com/pranay2611/accountservice

## Features
- CRUD operations on Account
- Account status update
- Account Balance update
- Customer projection 
- REST API endpoints
- PostgreSQL database
- Swagger API documentation
- RabbitMQ asynchronous messaging during account creation & updation


## Tech Stack
- Java 21
- Spring Boot 3.1.4
- PostgreSQL
- RabbitMQ
- Maven

## Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 14+
- RabbitMQ 3.12+

## Setup Instructions
### 1. Database Setup (if not using docker-compose)
CREATE DATABASE accounts;

CREATE ROLE account WITH LOGIN PASSWORD 'account';

CREATE DATABASE accounts OWNER account;

GRANT ALL PRIVILEGES ON DATABASE accounts TO account;

ALTER TABLE accounts OWNER TO account;

ALTER TABLE customer_projection OWNER TO account;

### 2. Configure Application
Edit `src/main/resources/application.properties`:
- Set PostgreSQL password
- Set Gmail credentials
- Configure RabbitMQ (default: localhost:5672)

### 3. Build and Run
**For local setup, run:**

mvn clean install  
mvn spring-boot:run

**Use docker-compose for containerized setup**

**Build the Project JAR:**
mvn clean package -DskipTests

**Run:** docker-compose up --build

### Launch Supporting Services with Docker Compose
Either start RabbitMQ locally, or
If you have a docker-compose.yml with RabbitMQ containers configured, run it (it will require application.properties endpoint changes).

Check containers with: docker ps

### Access Points
- Application: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html
- Health Check: http://localhost:8081/api/health


## API Endpoints
### Account
- POST `/api/accounts` - Create new account
- GET `/api/accounts/{id}` - Retrieve account by ID
- PUT `/api/accounts/{id}` - Update account by ID
- GET `/api/accounts/{id}/status` - Retrieve status of account by ID
- PATCH `/api/accounts/{id}/status` - Update status of account by ID
- GET `/api/accounts/{id}/balance` - Retrieve balance of account by ID
- PATCH `/api/accounts/{id}/balance` - Update balance of account by ID
- POST `/api/accounts/{id}/close` - Close account by ID


## RabbitMQ Queues
### Events Publishing On:
- `account.creation.queue` - Account create event
- `account.update.queue` - Account update event


## Test the API
Open http://localhost:8081/swagger-ui.html in your browser, or use Postman to send requests to your endpoints.
