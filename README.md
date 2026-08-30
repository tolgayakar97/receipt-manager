# Receipt Manager

Receipt Manager is a learning-oriented full-stack receipt management application.

The project allows users to upload receipts, extract receipt information with OCR, store the data securely per user, and manage receipts throughout their lifecycle. AI-powered spending analysis will be added in a later phase.

## Architecture

The project is designed as a Dockerized multi-service application:

- **Spring Boot** — main REST API, authentication, authorization and receipt management
- **Python + FastAPI** — OCR service powered by PaddleOCR
- **React** — frontend application
- **PostgreSQL** — persistent application data
- **Redis** — caching / supporting infrastructure
- **Kafka** — asynchronous event processing
- **Docker Compose** — local development and service orchestration

### Current development architecture

```text
                    ┌──────────────┐
                    │    React     │
                    │    :3000     │
                    └──────┬───────┘
                           │
                           ▼
                    ┌──────────────┐
                    │ Spring Boot  │
                    │    :8080     │
                    └──┬────┬──────┘
                       │    │
             ┌─────────┘    └──────────┐
             ▼                         ▼
      ┌─────────────┐           ┌─────────────┐
      │ PostgreSQL  │           │    Redis    │
      │    :5433    │           │    :6379    │
      └─────────────┘           └─────────────┘

                    ┌──────────────┐
                    │ OCR Service  │
                    │ Python +     │
                    │ PaddleOCR    │
                    │    :8000     │
                    └──────────────┘

                    ┌──────────────┐
                    │    Kafka     │
                    │    planned   │
                    └──────────────┘
```

Redis is already part of the Docker Compose infrastructure, but application-level caching is not implemented yet. Kafka and React are the next major integration steps.

## Current Goal — V1 MVP

The first version focuses on a simple and functional receipt management flow:

1. User registration and login
2. JWT-based authentication
3. User-specific receipt access
4. Receipt image upload
5. OCR service integration
6. Structured receipt data contract
7. Parsed receipt and item persistence
8. Receipt CRUD operations
9. Redis caching
10. Kafka-based event processing
11. React frontend
12. End-to-end MVP flow

The current development strategy intentionally postpones the OCR parser implementation. The OCR service temporarily returns a mock structured result using the same contract that the future parser will produce. This allows the Spring Boot, database, Redis, Kafka and React layers to be developed and tested end-to-end without blocking on parser implementation.

### Parsed receipt contract

The temporary mock parser returns the following structure:

```json
{
  "merchantName": "YUNUS MARKET ISLT.TIC.A.Ş.",
  "receiptNumber": "0039",
  "purchaseDate": "2026-08-29",
  "totalAmount": 405.73,
  "items": [
    {
      "name": "CUMHURIYET SUCUK KG",
      "quantity": 0.244,
      "unit": "KG",
      "unitPrice": 1519.80
    },
    {
      "name": "ULKER PETIBOR COKOK",
      "quantity": 1,
      "unit": "AD",
      "unitPrice": 34.90
    }
  ]
}
```

Spring Boot consumes this contract through `OcrResponse`, `ParsedReceipt` and `ReceiptItemDTO`. Parsed items are converted into the `ReceiptItem` entity before persistence. When the real parser is implemented, it should produce the same structure so downstream services do not need to change.

## Development Roadmap

### Phase 0 — Infrastructure

- [x] Create GitHub repository
- [x] Define initial architecture
- [x] Create Docker Compose setup
- [x] Dockerize Spring Boot
- [x] Dockerize PostgreSQL
- [x] Dockerize Redis
- [ ] Dockerize Kafka
- [x] Create Python OCR service container
- [ ] Dockerize React frontend
- [x] Configure Docker network and service communication

Current development containers:

| Service | Container | Host Port | Container Port |
|---|---|---:|---:|
| Spring Boot | `rm-backend` | `8080` | `8080` |
| PostgreSQL | `rm-postgres` | `5433` | `5432` |
| Redis | `rm-redis` | `6379` | `6379` |
| OCR Service | `rm-ocr` | `8000` | `8000` |

PostgreSQL data is persisted using the named `postgres_data` Docker volume. `docker compose down` preserves database data, while `docker compose down -v` removes the volume and its persisted data.

The OCR service uses a named `paddlex_cache` Docker volume for PaddleX/PaddleOCR model files. This prevents models from being downloaded again whenever the OCR container is recreated. The first startup may download the required models.

During OCR development, `./ocr-service` is mounted into the container and Uvicorn runs with `--reload`, so Python source changes are picked up without rebuilding the Docker image.

### Phase 1 — Spring Boot Foundation

- [x] Create Spring Boot application
- [x] Configure REST API
- [x] Configure PostgreSQL connection
- [x] Add JPA / Hibernate dependency
- [x] Add Flyway and establish migration-first schema management
- [x] Create initial database migration for the User table
- [x] Configure Hibernate schema validation (`ddl-auto=validate`)
- [x] Create User entity and map it to the migration-managed `users` table
- [x] Implement user registration
- [x] Create Receipt entity
- [x] Create Receipt database migration
- [x] Define User → Receipt relationship
- [x] Configure database-generated Receipt creation timestamp

Flyway is the source of truth for database structure. Hibernate is configured with `ddl-auto=validate`, so it validates the schema without creating or modifying tables.

The Receipt schema is introduced by `V2__create_receipt_table.sql`. It contains a generated ID, file path, name, optional description, creation timestamp, soft-delete flag, and a non-null foreign key to `users(id)`.

The `created_at` value is generated by PostgreSQL using `DEFAULT CURRENT_TIMESTAMP` and populated back into the entity on insert.

### Phase 2 — Authentication

- [x] Password hashing
- [x] User registration
- [x] Login endpoint
- [x] Authentication with `AuthenticationManager`
- [x] `UserDetailsService` implementation
- [x] JWT generation
- [x] JWT authentication filter
- [x] Authenticated request handling
- [x] User-specific authorization

The authentication flow uses Spring Security's `AuthenticationManager` and a custom `UserDetailsService` to authenticate users by email and password. Successful login generates a signed JWT using JJWT.

The token is read from the `Authorization: Bearer <token>` header by a `OncePerRequestFilter`, validated, and used to create an `Authentication` object stored in the `SecurityContext`. Protected endpoints require an authenticated request. The current token expiration is 1 hour.

Authenticated receipt operations resolve the current user from the `SecurityContext` using the email available from `Authentication`.

### Phase 3 — Receipt Management & Persistence

- [x] Receipt creation
- [x] Receipt listing
- [x] Receipt filtering by deletion state
- [x] Receipt detail
- [x] Receipt update
- [x] Receipt deletion (soft delete)
- [ ] Receipt status
- [ ] Physical receipt file storage
- [x] Multipart receipt image upload support
- [x] Persist parsed receipt information in the database
- [x] Persist receipt items in a separate table
- [ ] Category information

Receipt creation assigns the authenticated user as the receipt owner, so the client does not provide a user ID.

Receipt listing is implemented through `GET /receipts`. The endpoint accepts the `isDeleted` query parameter:

- `GET /receipts?isDeleted=false` — active receipts
- `GET /receipts?isDeleted=true` — soft-deleted receipts / trash

Receipt detail, update and deletion operations are scoped to the authenticated user. Receipt lookup uses both the receipt ID and the authenticated user's ID, preventing users from accessing or modifying receipts owned by another user.

Receipt deletion is implemented as a soft delete by setting the receipt's deletion flag instead of physically removing the database row. Successful deletion returns `204 No Content`.

The receipt creation endpoint accepts `multipart/form-data` through `ReceiptRequest`, including a `MultipartFile` field. Upload-size configuration is also supported. Physical file storage is intentionally kept as a separate step so the current MVP can focus on the OCR and structured receipt flow.

Parsed receipt data is persisted directly onto the `receipt` table using the fields `merchant_name`, `receipt_number`, `purchase_date` and `total_amount`. Individual purchased items are stored in the separate `receipt_item` table with a `receipt_id` foreign key.

The JPA relationship is modeled as `Receipt` → `ReceiptItem` with `@OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)` and `ReceiptItem` → `Receipt` with `@ManyToOne`. The `Receipt.addReceiptItem(...)` helper keeps both sides of the relationship synchronized.

Database migrations for parsed receipt persistence are:

- `V3__create_receipt_item_table.sql` — creates the `receipt_item` table and its foreign key to `receipt(id)`.
- `V4__add_ocr_fields_to_receipt.sql` — adds the parsed receipt fields to `receipt`.

The current OCR-to-database flow is validated with a real receipt image and the mock structured parser response.

### Phase 4 — OCR Service

- [x] Create FastAPI service
- [x] Integrate PaddleOCR
- [x] Create OCR endpoint
- [x] Accept image uploads such as JPEG/PNG
- [x] Extract raw OCR text through `rec_texts`
- [x] Dockerize OCR service
- [x] Configure OCR development hot reload
- [x] Persist PaddleOCR model cache across container recreation
- [x] Define structured `ParsedReceipt` contract
- [x] Send receipt image from Spring Boot to OCR service
- [x] Deserialize parsed receipt data in Spring Boot
- [ ] Parse raw OCR text into structured receipt data
- [x] Persist parsed receipt information and items

The OCR service accepts an uploaded image, writes it to a temporary file and is integrated with Spring Boot through the `/ocr` endpoint. The real PaddleOCR `rec_texts` output has been validated with a test receipt.

The parser implementation is intentionally deferred. The current `/ocr` response simulates the parser output using a fixed `parsedReceipt` object so the rest of the application can be developed against the final data contract.

The Spring Boot OCR client sends the receipt image as `multipart/form-data` and deserializes the response into `OcrResponse`, `ParsedReceipt` and `ReceiptItemDTO`. The contract currently contains merchant name, receipt number, purchase date, total amount and essential item information.

### Phase 5 — Redis

- [ ] Define Redis use cases
- [ ] Configure Spring Data Redis
- [ ] Add receipt caching
- [ ] Add cache invalidation/update behavior
- [ ] Verify cache-backed receipt reads

Redis is already available as the `rm-redis` Docker Compose service. Application-level integration is the next backend task.

### Phase 6 — Kafka

- [ ] Add Kafka to Docker Compose
- [ ] Configure Kafka in Spring Boot
- [ ] Define receipt/OCR event contract
- [ ] Implement Kafka producer
- [ ] Implement Kafka consumer
- [ ] Introduce asynchronous processing where useful
- [ ] Verify event flow end-to-end

### Phase 7 — React Frontend

- [ ] Create React application
- [ ] Login / register UI
- [ ] JWT handling
- [ ] Receipt upload UI
- [ ] Receipt list
- [ ] Receipt detail
- [ ] Receipt update / delete
- [ ] Trash view
- [ ] Connect frontend to backend APIs

### Phase 8 — End-to-End MVP

- [ ] Run complete flow from React to Spring Boot
- [ ] Upload a real receipt image
- [ ] OCR and mock parsed result
- [x] Persist parsed receipt and receipt items
- [ ] Publish / consume Kafka events
- [ ] Use Redis in the application flow
- [ ] Display persisted receipt data in React
- [ ] Verify the complete Dockerized flow

### Phase 9 — Real Parser & AI Analysis

- [ ] Parse raw OCR text into the existing `ParsedReceipt` contract
- [ ] Replace the temporary mock parser
- [ ] Spending analysis
- [ ] Category-based spending statistics
- [ ] Monthly spending trends
- [ ] Store-based analysis
- [ ] Most frequently purchased products
- [ ] Unusual spending detection
- [ ] Natural-language spending summaries

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot |
| Authentication | Spring Security, JWT (JJWT) |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Database Migrations | Flyway |
| OCR Service | Python, FastAPI, PaddleOCR |
| Frontend | React |
| Cache | Redis 8 |
| Messaging | Apache Kafka |
| Containerization | Docker, Docker Compose |

## Development Approach

This project is also used as a learning project. Features are implemented incrementally rather than building the complete architecture at once.

The immediate development order is:

1. ~~Flyway and migration-first database schema~~ **Completed**
2. ~~User entity and persistence layer~~ **Completed**
3. ~~User registration~~ **Completed**
4. ~~Spring Security authentication~~ **Completed**
5. ~~JWT authentication filter~~ **Completed**
6. ~~Authenticated request handling and authorization~~ **Completed**
7. ~~Receipt entity and User → Receipt relationship~~ **Completed**
8. ~~Receipt creation and database-generated creation timestamp~~ **Completed**
9. ~~Receipt listing and deletion-state filtering~~ **Completed**
10. ~~Receipt detail / update / delete~~ **Completed**
11. ~~Multipart receipt image upload support~~ **Completed**
12. ~~Python OCR service and PaddleOCR integration~~ **Completed**
13. ~~Spring Boot → OCR service integration~~ **Completed**
14. ~~Structured parsed receipt contract and DTO mapping~~ **Completed (mock parser)**
15. ~~Persist parsed receipt information and receipt items~~ **Completed**
16. Redis integration
17. Kafka integration
18. React frontend
19. End-to-end MVP flow
20. Real OCR result parser
21. AI-powered spending analysis

The real parser is intentionally placed after the end-to-end MVP milestones. It will replace the mock parser while preserving the existing `ParsedReceipt` contract.
