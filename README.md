# Concert Ticket Booking Marketplace — Architecture & Setup

## Architecture Overview

The system follows **Clean Architecture** and **Domain Driven Design (DDD)** principles to ensure maintainability, scalability, and testability.

### Layers

1.  **Domain Layer** (`domain/`): Contains core entities (Concert, Booking, Ledger) and business rules.
2.  **API/Controller Layer** (`controller/`): REST endpoints, JWT authentication, and request validation.
3.  **Service Layer** (`service/`): Orchestrates business logic, including high-concurrency inventory locking and dynamic pricing.
4.  **Data Access Layer** (`repository/`): JPA repositories with pessimistic locking support for transactional integrity.
5.  **Messaging Layer** (`messaging/`): RabbitMQ integration for async event processing (e.g., lock expiry).

### Core Features

*   **High Concurrency**: Uses PostgreSQL **Pessimistic Locking** (`SELECT FOR UPDATE`) to prevent overselling during peak booking periods. Tested for atomic stock decrement under heavy load.
*   **Dynamic Pricing**: Pricing scales automatically based on real-time ticket availability using an inverse-ratio formula.
*   **Idempotency**: All booking requests are processed using an `Idempotency-Key` header to prevent duplicate transactions.
*   **Immutable Ledger**: Financial transactions are written to an append-only ledger table protected by database triggers to prevent updates/deletes.
*   **Observability**: Implementation includes JSON structured logging, MDC-based **Correlation IDs**, and Spring Boot Actuator health checks.

---

## Tech Stack

*   **Java 21** / **Spring Boot 3.2**
*   **PostgreSQL 16** (Primary DB)
*   **Redis 7** (Pricing & Search Cache)
*   **RabbitMQ 3** (Event Bus)
*   **Flyway** (Schema Migrations)
*   **Bucket4j** (Rate Limiting)
*   **MapStruct** / **Lombok**

---

## Setup & Running

### 1. Infrastructure
Ensure Docker is installed, then run:
```bash
docker-compose up -d
```
This starts PostgreSQL (port 5434), Redis (port 6379), and RabbitMQ (port 5672).


### 2. Run Application
```bash
mvn spring-boot:run
```

### 3. API Documentation
Open Swagger UI at:
`http://localhost:8081/swagger-ui.html`


### 4. Run Tests
```bash
mvn test
```

---

## API Flow Example

1. **Register/Login**: POST `/api/v1/auth/register` → returns JWT.
2. **Search**: GET `/api/v1/concerts/search?name=Rock`
3. **Book**: POST `/api/v1/bookings` with `Idempotency-Key` and JWT.
4. **Pay**: POST `/api/v1/bookings/{id}/pay`
5. **Dashboard**: GET `/api/v1/analytics/dashboard`
```
