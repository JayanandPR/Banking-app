# Bankapp — Spring Boot Banking API

A RESTful banking backend built with Spring Boot. It provides user registration and login, JWT-based authentication, account operations, and transaction history backed by PostgreSQL.


## Features

- User registration and login using email and password
- JWT-based stateless authentication
- Password hashing with BCrypt
- PostgreSQL persistence with Spring Data JPA / Hibernate
- Account balance lookup
- Deposit and withdrawal operations
- Transfers between accounts
- Transaction history
- Request validation and centralized exception handling
- Lombok to reduce boilerplate code

## Tech Stack

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- JSON Web Tokens (JJWT 0.12.6)
- Jakarta Bean Validation
- Lombok
- Maven

## Prerequisites

Install the following before running the application:

- JDK 25 (the version configured in `pom.xml`)
- PostgreSQL
- IntelliJ IDEA or another Java IDE
- Git (optional, for version control)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
cd YOUR_REPOSITORY
```

Replace the URL with the URL of your GitHub repository.

### 2. Create the PostgreSQL database

Open PostgreSQL and create a database named `bankapp` (or update `DB_URL` to match your chosen database).

```sql
CREATE DATABASE bankapp;
```

### 3. Configure environment variables

The application reads its database credentials and JWT signing key from environment variables referenced in `src/main/resources/application.properties`.

Configure these variables in your IDE's Spring Boot run configuration or in your shell:

| Variable | Purpose | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/bankapp` |
| `DB_USERNAME` | PostgreSQL username | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | Set your local password |
| `JWT_SECRET` | JWT signing key | Generate a strong random key of at least 32 bytes for HS256 |

Do not commit real credentials or signing keys to GitHub. A regular `.env` file is **not** loaded automatically by Spring Boot; configure the variables in IntelliJ or explicitly add dotenv support.

Your `application.properties` should use placeholders similar to:

```properties
spring.application.name=bankapp

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=${JWT_SECRET}
jwt.expiration-ms=864000000
```

`jwt.expiration-ms` is the token lifetime in milliseconds (10 days in this configuration). For production, review database schema management and avoid logging sensitive SQL or data.

### 4. Run the application

In IntelliJ IDEA:

1. Open the project and allow Maven to import dependencies.
2. Set the project SDK to JDK 25.
3. Create or select the Spring Boot run configuration for `com.example.bankapp.BankappApplication`.
4. Add the four environment variables listed above to that run configuration.
5. Run `BankappApplication`.

Alternatively, if Maven is installed and the environment variables are configured in your terminal:

```bash
mvn spring-boot:run
```

The API will use Spring Boot's default port, `8080`, unless configured otherwise.

## API Endpoints

Base URL: `http://localhost:8080`

### Authentication

These endpoints are public and do not require a JWT.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a user and create an account |
| `POST` | `/api/auth/login` | Log in and receive an authentication response/token |

**Register example**

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "name": "Alex Morgan",
  "email": "alex@example.com",
  "password": "change-me-123"
}
```

**Login example**

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "alex@example.com",
  "password": "change-me-123"
}
```

Use the token returned by the login/register response for protected requests. The usual header format is:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

### Accounts

All account endpoints require authentication.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/accounts/me` | Get the authenticated user's account |
| `POST` | `/api/accounts/deposit` | Deposit money into the user's account |
| `POST` | `/api/accounts/withdraw` | Withdraw money from the user's account |
| `POST` | `/api/accounts/transfer` | Transfer money to another account |

**Deposit / withdrawal body**

```json
{
  "amount": 100.00
}
```

**Transfer body**

```json
{
  "toAccountNumber": "RECIPIENT_ACCOUNT_NUMBER",
  "amount": 50.00
}
```

Amounts must be greater than zero and have at most two decimal places.

### Transactions

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/transactions` | Get the authenticated user's transaction history |

Send the JWT in the `Authorization` header for all account and transaction requests. Unauthenticated requests to protected endpoints return `401 Unauthorized`.

## Project Structure

```text
src/main/java/com/example/bankapp/
├── account/       # Account endpoints, services, DTOs and repository
├── auth/          # Registration and login endpoints and services
├── common/        # API exception handling
├── config/        # Spring Security configuration
├── security/      # JWT service and authentication filter
├── transaction/   # Transaction entity, service and endpoints
├── user/          # User entity and repository
└── BankappApplication.java

src/main/resources/
└── application.properties
```

## Security and Configuration Notes

- Keep `.env`, local configuration files containing secrets, and real credentials out of version control.
- Use a strong, randomly generated JWT key; do not use a sample or publicly shared key.
- Use HTTPS, production-grade secret management, and migrations such as Flyway or Liquibase before deploying a real application.
- `spring.jpa.hibernate.ddl-auto=update` is convenient for development but should be reviewed before production use.
- This project is educational and has not been represented as audited or production-ready financial software.

## License

No license has been specified yet. Add a license file if you intend to define reuse and distribution permissions.
