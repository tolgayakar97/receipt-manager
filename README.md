# Receipt Manager

Receipt Manager is a learning-oriented full-stack receipt management application built around a Dockerized, asynchronous backend architecture.

The current backend MVP supports user authentication, receipt upload and persistence, OCR processing through a separate Python service, Redis caching, Kafka-based asynchronous processing, and persistence of parsed receipt data and receipt items in PostgreSQL.

**Backend end-to-end flow is complete and verified locally. The next major milestone is the React frontend.**

## Architecture

```text
                         ┌──────────────────┐
                         │      React       │
                         │    Frontend      │
                         │     planned      │
                         └────────┬─────────┘
                                  │ HTTP
                                  ▼
                         ┌──────────────────┐
                         │   Spring Boot    │
                         │    REST API      │
                         │      :8080       │
                         └───────┬──────────┘
                                 │
              ┌──────────────────┼───────────────────┐
              │                  │                   │
              ▼                  ▼                   ▼
       ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
       │ PostgreSQL  │    │    Redis    │    │    Kafka    │
       │    :5433    │    │    :6379    │    │    :9092    │
       └─────────────┘    └─────────────┘    └──────┬──────┘
                                                    │
                                           receipt-created
                                                    │
                                                    ▼
                                           ┌────────────────┐
                                           │ Kafka Consumer  │
                                           └───────┬────────┘
                                                   │
                                                   ▼
                                           ┌────────────────┐
                                           │  OCR Service   │
                                           │ FastAPI +      │
                                           │ PaddleOCR      │
                                           │     :8000      │
                                           └────────────────┘
```

### Receipt creation flow

1. An authenticated client uploads an image to `POST /receipts` using `multipart/form-data`.
2. Spring Boot stores the file under `/app/uploads` with a UUID-based filename.
3. The receipt is persisted in PostgreSQL and associated with the authenticated user.
4. Spring Boot publishes a `ReceiptCreatedEvent` to the `receipt-created` Kafka topic.
5. The Kafka consumer loads the receipt and reads the stored image.
6. The consumer sends the image to the OCR service.
7. PaddleOCR processes the image. The parser currently returns a structured mock result.
8. Spring Boot persists the parsed receipt fields and receipt items.

OCR processing is asynchronous; receipt creation does not wait for OCR completion.

## Current Status

### Implemented

- User registration and password hashing
- Login and JWT authentication
- User-specific authorization
- Receipt CRUD operations
- Soft deletion / trash flow
- Multipart receipt image upload
- Persistent uploaded-file storage
- PostgreSQL persistence
- Flyway migration-first schema management
- FastAPI + PaddleOCR service
- Spring Boot → OCR integration
- Structured parsed receipt contract
- Parsed receipt and receipt-item persistence
- Redis receipt-list caching
- Kafka producer and consumer
- Asynchronous receipt-created → OCR processing
- End-to-end backend flow verified locally

### Next / Not implemented yet

- React frontend
- Real OCR text parser (current parser is mocked)
- Receipt categories
- Receipt status
- Redis cache invalidation after mutations
- Pagination and advanced filtering
- AI-powered spending analysis

## API

Receipt endpoints require a JWT in the `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/register` | Register a user |
| `POST` | `/login` | Authenticate and receive a JWT |

### Receipts

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/receipts` | Upload and create a receipt |
| `GET` | `/receipts?isDeleted=false` | List active receipts |
| `GET` | `/receipts?isDeleted=true` | List soft-deleted receipts |
| `GET` | `/receipts/{id}?isDeleted=false` | Get an active receipt |
| `GET` | `/receipts/{id}?isDeleted=true` | Get a deleted receipt |
| `PUT` | `/receipts/{id}` | Update receipt metadata |
| `DELETE` | `/receipts/{id}` | Soft-delete a receipt |

`POST /receipts` accepts `multipart/form-data`. The client supplies the receipt image, name, and description; the backend creates and stores the file path itself.

Receipt operations are scoped to the authenticated user, preventing access to receipts owned by another user.

## OCR Processing

The OCR service is implemented with FastAPI and PaddleOCR. It accepts an uploaded image and currently exposes a structured response contract for downstream Spring Boot processing.

The current response contract is:

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

Spring Boot maps the response through `OcrResponse`, `ParsedReceipt`, and `ReceiptItemDTO`. The real parser will replace the mock implementation while preserving this contract.

## Redis

Redis caches receipt lists returned by `GET /receipts`.

Cache keys are user- and deletion-state-specific:

```text
receipts:user:{userId}:{isDeleted}
```

A cache hit returns the cached `List<ReceiptResponse>` directly. A cache miss loads PostgreSQL data and stores the result in Redis.

The application uses a typed `RedisTemplate<String, List<ReceiptResponse>>` with Jackson JSON serialization.

Cache invalidation after create/update/delete is a planned improvement.

## Kafka

Kafka decouples receipt creation from OCR processing.

**Topic:** `receipt-created`  
**Consumer group:** `receipt-manager`

Current event:

```json
{
  "receiptId": 15,
  "userId": 1,
  "filePath": "/app/uploads/550e8400-e29b-41d4-a716-446655440000_receipt.jpg"
}
```

The event is published after the receipt and file are persisted. The consumer loads the receipt, reads the stored file, sends it to OCR, and persists the parsed result.

Kafka runs as a single-node KRaft broker using `apache/kafka:4.0.1`.

## File Storage

Uploaded files are stored inside the backend container at:

```text
/app/uploads
```

The directory is backed by the Docker Compose named volume `uploads`, so uploaded receipt files persist across container recreation. Files use UUID-prefixed filenames to avoid collisions.

## Database

Flyway is the source of truth for database structure. Hibernate uses `ddl-auto=validate`, so it validates the schema without creating or modifying tables.

Current migrations:

- `V1` — user table
- `V2` — receipt table
- `V3` — receipt item table
- `V4` — parsed OCR fields on receipt

The JPA relationship is:

```text
Receipt 1 ─────── * ReceiptItem
```

with cascading persistence and orphan removal.

## Docker Services

| Service | Container | Host Port | Container Port |
|---|---|---:|---:|
| Spring Boot | `rm-backend` | `8080` | `8080` |
| PostgreSQL | `rm-postgres` | `5433` | `5432` |
| Redis | `rm-redis` | `6379` | `6379` |
| OCR Service | `rm-ocr` | `8000` | `8000` |
| Kafka | `rm-kafka` | `9092` | `9092` |

### Persistent volumes

| Volume | Purpose |
|---|---|
| `postgres_data` | PostgreSQL data |
| `maven_cache` | Maven dependency cache |
| `paddlex_cache` | PaddleOCR / PaddleX model cache |
| `uploads` | Uploaded receipt files |

`docker compose down` preserves named volumes. `docker compose down -v` removes persisted volume data.

The OCR source directory is mounted into the container and Uvicorn runs with `--reload` for development hot reload.

## Getting Started

### Requirements

- Docker
- Docker Compose
- Git

### 1. Clone

```bash
git clone https://github.com/tolgayakar97/receipt-manager.git
cd receipt-manager
```

### 2. Configure environment

```bash
cp .env.example .env
```

Set the PostgreSQL credentials and JWT secret in `.env`.

### 3. Start the application

```bash
docker compose up --build
```

Services:

- Spring Boot: `http://localhost:8080`
- OCR service: `http://localhost:8000`
- PostgreSQL: `localhost:5433`
- Redis: `localhost:6379`
- Kafka: `localhost:9092`

The first OCR startup may take longer because PaddleOCR/PaddleX models can be downloaded into the persistent `paddlex_cache` volume.

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.1.1 |
| Security | Spring Security, JWT (JJWT) |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway |
| OCR Service | Python, FastAPI, PaddleOCR |
| Cache | Redis 8 |
| Messaging | Apache Kafka 4.0.1 |
| Frontend | React *(planned)* |
| Containerization | Docker, Docker Compose |

## Development Roadmap

### Phase 0 — Infrastructure

- [x] GitHub repository and architecture
- [x] Docker Compose
- [x] Dockerized Spring Boot
- [x] Dockerized PostgreSQL
- [x] Dockerized Redis
- [x] Dockerized Kafka
- [x] Dockerized OCR service
- [x] Service communication
- [ ] Dockerize React frontend

### Phase 1 — Spring Boot Foundation

- [x] REST API
- [x] PostgreSQL / JPA / Hibernate
- [x] Flyway migration-first schema
- [x] User entity and persistence
- [x] Receipt entity and persistence
- [x] User → Receipt relationship
- [x] Database-generated receipt timestamp

### Phase 2 — Authentication

- [x] Password hashing
- [x] Registration
- [x] Login
- [x] `AuthenticationManager`
- [x] `UserDetailsService`
- [x] JWT generation
- [x] JWT authentication filter
- [x] Authenticated request handling
- [x] User-specific authorization

### Phase 3 — Receipt Management

- [x] Receipt creation
- [x] Receipt listing
- [x] Deletion-state filtering
- [x] Receipt detail
- [x] Receipt update
- [x] Soft deletion
- [x] Multipart image upload
- [x] Persistent file storage
- [x] Parsed receipt persistence
- [x] Receipt item persistence
- [ ] Pagination and advanced filtering
- [ ] Receipt status
- [ ] Category information

### Phase 4 — OCR Service

- [x] FastAPI service
- [x] PaddleOCR integration
- [x] Image upload endpoint
- [x] JPEG/PNG processing
- [x] Raw OCR text extraction
- [x] Dockerized OCR service
- [x] PaddleOCR model caching
- [x] Structured `ParsedReceipt` contract
- [x] Spring Boot → OCR integration
- [x] OCR response persistence
- [ ] Parse raw OCR text into structured data
- [ ] Replace temporary mock parser

### Phase 5 — Redis

- [x] Redis integration
- [x] Receipt-list caching for `GET /receipts`
- [x] User/deletion-state-specific cache keys
- [x] Typed Redis serialization
- [x] Cache-backed receipt reads
- [ ] Cache invalidation after create/update/delete

### Phase 6 — Kafka

- [x] Kafka Docker service
- [x] Spring Kafka configuration
- [x] `ReceiptCreatedEvent`
- [x] Kafka producer
- [x] Kafka consumer
- [x] Asynchronous receipt → OCR processing
- [x] Shared uploaded-file flow
- [x] Producer → Kafka → consumer verification

### Phase 7 — Backend End-to-End MVP

- [x] Upload real receipt image
- [x] Persist uploaded file
- [x] Persist receipt metadata
- [x] Publish receipt-created event
- [x] Consume event asynchronously
- [x] Send stored image to OCR service
- [x] Receive structured OCR response
- [x] Persist parsed receipt data
- [x] Persist receipt items
- [x] Redis-backed receipt reads
- [x] Verify complete backend flow locally

### Phase 8 — React Frontend

- [ ] Create React application
- [ ] Login / registration UI
- [ ] JWT handling
- [ ] Receipt upload UI
- [ ] Receipt list
- [ ] Receipt detail
- [ ] Receipt update / delete
- [ ] Trash view
- [ ] Connect frontend to backend APIs
- [ ] Display asynchronously processed OCR results

### Phase 9 — Real Parser & AI Analysis

- [ ] Parse raw OCR text into `ParsedReceipt`
- [ ] Replace mock parser
- [ ] Spending analysis
- [ ] Category-based spending statistics
- [ ] Monthly spending trends
- [ ] Store-based analysis
- [ ] Frequently purchased products
- [ ] Unusual spending detection
- [ ] Natural-language spending summaries

## Development Approach

Receipt Manager is intentionally developed incrementally as a learning project.

The current milestone is the **backend end-to-end MVP**. The complete path from receipt upload to asynchronous Kafka processing, OCR, Redis-backed reads, and PostgreSQL persistence is working locally.

The next milestone is the React frontend. The real OCR parser will be implemented after frontend integration so the existing `ParsedReceipt` contract can remain stable while the parser evolves.

## License

This project is licensed under the terms of the repository's `LICENSE` file.
