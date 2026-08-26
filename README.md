# Receipt Manager

Receipt Manager is a learning-oriented full-stack receipt management application.

The project will allow users to upload receipts, extract text and receipt information with OCR, store the data securely per user, and manage receipts throughout their lifecycle. AI-powered spending analysis will be added in a later phase.

## Architecture

The project is designed as a Dockerized multi-service application:

- **Spring Boot** — main REST API, authentication, authorization and receipt management
- **Python + FastAPI** — OCR service powered by PaddleOCR
- **React** — frontend application
- **PostgreSQL** — persistent application data
- **Redis** — caching / supporting infrastructure
- **Kafka** — asynchronous event processing
- **Docker Compose** — local development and service orchestration

### Planned architecture

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
      └─────────────┘           └─────────────┘
                                      
                               ┌─────────────┐
                               │    Kafka    │
                               └──────┬──────┘
                                      │
                                      ▼
                               ┌─────────────┐
                               │ OCR Service │
                               │ Python +    │
                               │ PaddleOCR   │
                               └─────────────┘
```

## Current Goal — V1

The first version focuses on a simple and functional receipt management flow:

1. User registration and login
2. JWT-based authentication
3. User-specific receipt access
4. Receipt image upload
5. OCR text extraction
6. Receipt information storage
7. Receipt CRUD operations
8. Receipt status management
9. Basic categories and amount information
10. Dockerized development environment

### Receipt information

A receipt is planned to contain information such as:

- Receipt ID
- User
- Image reference
- OCR text
- Store name
- Category
- Amount
- Purchase date
- Status
- Created / updated timestamps

## Development Roadmap

### Phase 0 — Infrastructure

- [x] Create GitHub repository
- [x] Define initial architecture
- [x] Create Docker Compose setup
- [x] Dockerize Spring Boot
- [x] Dockerize PostgreSQL
- [x] Dockerize Redis
- [ ] Dockerize Kafka
- [ ] Create Python OCR service container
- [ ] Dockerize React frontend
- [x] Configure Docker network and service communication

Current development containers:

| Service | Container | Host Port | Container Port |
|---|---|---:|---:|
| Spring Boot | `rm-backend` | `8080` | `8080` |
| PostgreSQL | `rm-postgres` | `5433` | `5432` |
| Redis | `rm-redis` | `6379` | `6379` |

Kafka is intentionally not included in the initial Compose setup. It will be introduced when asynchronous processing becomes necessary.

### Phase 1 — Spring Boot Foundation

- [x] Create Spring Boot application
- [x] Configure REST API
- [x] Configure PostgreSQL connection
- [x] Add JPA / Hibernate dependency
- [ ] Add Flyway and establish migration-first schema management
- [ ] Create initial database migration for the User table
- [ ] Configure Hibernate schema validation (`ddl-auto=validate`)
- [ ] Create User entity and map it to the migration-managed table
- [ ] Implement basic user APIs

**Schema management rule:** Database tables are not created manually and Hibernate must not create or modify the schema. Flyway migrations are the source of truth for database structure. New schema changes will be introduced through new versioned migrations rather than modifying previous migrations.

The Spring Boot application currently runs inside Docker using Maven and Spring Boot DevTools. The backend source directory is bind-mounted into the container, so normal code changes do not require rebuilding the Docker image during development.

### Phase 2 — Authentication

- [ ] Password hashing
- [ ] User registration
- [ ] Login endpoint
- [ ] JWT generation
- [ ] JWT authentication filter
- [ ] Authenticated request handling
- [ ] User-specific authorization

### Phase 3 — Receipt Management

- [ ] Receipt entity
- [ ] User → Receipt relationship
- [ ] Receipt creation
- [ ] Receipt listing
- [ ] Receipt detail
- [ ] Receipt update
- [ ] Receipt deletion
- [ ] Receipt status
- [ ] Image upload / storage

### Phase 4 — OCR Service

- [ ] Create FastAPI service
- [ ] Integrate PaddleOCR
- [ ] Create OCR endpoint
- [ ] Send receipt image from Spring Boot to OCR service
- [ ] Process OCR result
- [ ] Store extracted receipt information

### Phase 5 — React Frontend

- [ ] Create React application
- [ ] Login / register UI
- [ ] JWT handling
- [ ] Receipt upload UI
- [ ] Receipt list
- [ ] Receipt detail
- [ ] Receipt update / delete

### Phase 6 — Redis & Kafka

- [ ] Define Redis use cases
- [ ] Add caching where useful
- [ ] Define receipt/OCR events
- [ ] Introduce asynchronous processing where useful

### Phase 7 — AI Analysis

Future features may include:

- Spending analysis
- Category-based spending statistics
- Monthly spending trends
- Store-based analysis
- Most frequently purchased products
- Unusual spending detection
- Natural-language spending summaries

## Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot |
| Authentication | Spring Security, JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Database Migrations | Flyway |
| OCR Service | Python, FastAPI, PaddleOCR |
| Frontend | React |
| Cache | Redis |
| Messaging | Apache Kafka |
| Containerization | Docker, Docker Compose |

## Development Approach

This project is also used as a learning project. Features will be implemented incrementally rather than building the complete architecture at once.

The immediate development order is:

1. Flyway and migration-first database schema
2. User entity and persistence layer
3. Basic user APIs
4. Spring Security
5. JWT authentication
6. Receipt management
7. Python OCR service
8. React frontend
9. Redis / Kafka where needed
10. AI-powered spending analysis

The Python OCR service, React frontend, Redis, Kafka and AI features will be introduced progressively as the core backend becomes functional.
