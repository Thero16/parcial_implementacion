# 🕵️ Detective Case Management System

> A microservices-based web system for managing criminal investigations in the fictional city of **"Las Cariñosas"** — built with Spring Boot, Keycloak, Eureka Service Discovery, and a Vite/React frontend, all orchestrated with Docker Compose.

---

## 📖 Project Context

In the opulent city of *Las Cariñosas*, a serial killer has emerged from the shadows, targeting wealthy families and leaving behind a trail of death and mystery. The investigation falls on the shoulders of **Detective Luis R**, a man tormented by his own demons.

This system was built to support detective teams, analysts, and administrators as they work concurrently on cases — managing victims, suspects, witnesses, evidence, and tasks, with full access control and traceability.

---

## 🏗️ Architecture Overview

The system follows a **microservices architecture** where each service has its own responsibility and persistence layer. All communication goes through a single API Gateway entry point.

```
┌─────────────────────────────────────────────┐
│             Frontend (Vite/React)            │
│               localhost:5173                 │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│              API Gateway                     │
│              localhost:9000                  │
└──────┬─────────────┬──────────────┬─────────┘
       │             │              │
┌──────▼──────┐ ┌────▼──────┐ ┌────▼───────────┐
│Case Service │ │  People   │ │Evidence Service │
│             │ │  Service  │ │                 │
└──────┬──────┘ └────┬──────┘ └────┬────────────┘
       │             │              │
┌──────▼─────────────▼──────────────▼───────────┐
│                  PostgreSQL                    │
│     cases_db | people_db | evidences_db        │
└────────────────────────────────────────────────┘

┌───────────────────────┐   ┌────────────────────┐
│  Keycloak (Auth)      │   │  Eureka Discovery  │
│  localhost:8080       │   │  localhost:8761    │
└───────────────────────┘   └────────────────────┘
```

### Services

| Service | Responsibility |
|---|---|
| **API Gateway** | Single entry point for all frontend requests, JWT validation |
| **Case Service** | Case lifecycle management (status, priority, assignment) |
| **People Service** | Manages people in a case (victims, suspects, witnesses) |
| **Evidence Service** | Evidence management and chain of custody (includes attachments) |
| **Keycloak** | Authentication and role-based authorization |
| **Eureka** | Service discovery and registration |
| **PostgreSQL** | Persistent storage, one database per service |

### Communication Patterns

**Synchronous REST (HTTP):**
- `GET /cases/{id}` → Case Service
- `GET /cases/{id}/evidences` → Evidence Service
- `POST /evidences` → Evidence Service
- `POST /tasks` → Workflow Service

**Asynchronous Event-Driven (Pub/Sub):**

| Event | Publisher | Consumers |
|---|---|---|
| `CaseCreated` | Case Service | Workflow Service (creates initial checklist), Audit Service |
| `EvidenceAdded` | Evidence Service | Notification Service (alerts team), Audit Service |
| `TaskAssigned` | Workflow Service | Notification Service, Audit Service |
| `TaskOverdue` | Workflow Service | Notification Service, Audit Service |

---

## 🔐 Roles & Permissions

| Role | Permissions |
|---|---|
| `ADMIN` | Full access — can create, read, update, and **delete** cases and evidence |
| `DETECTIVE` | Can manage cases, assign tasks, add evidence |
| `VIEWER` | Read-only access to cases and evidence |

> **Business rules:**
> - Only `ADMIN` users can delete cases or evidence.
> - Tasks can be assigned to users with the `DETECTIVE` role.
> - Evidence belongs to a case and must preserve a chain of custody history.

---

## ⚙️ Prerequisites

- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed
- Ports available: `5173`, `8080`, `8761`, `9000`, `5432`

---

## 🚀 Getting Started

### 1. Configure Environment Variables

The project requires **two `.env` files** before running.

#### 📁 Root `.env` — place at the project root, alongside `docker-compose.yml`

```env
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=postgres

DB_HOST=postgres
DB_PORT=5432
DB_USER=postgres
DB_PASSWORD=postgres

# Database names per service
CASE_DB_NAME=cases
EVIDENCE_DB_NAME=evidences
PEOPLE_DB_NAME=people
KEYCLOAK_DB_NAME=keycloak

# Keycloak Admin Credentials
KEYCLOAK_ADMIN=admin
KEYCLOAK_ADMIN_PASSWORD=admin

# Keycloak DB connection
KC_DB=postgres
KC_DB_URL=jdbc:postgresql://postgres:5432/keycloak
KC_DB_USERNAME=postgres
KC_DB_PASSWORD=postgres
KC_HTTP_PORT=8080

# Keycloak configuration
KEYCLOAK_REALM=spring
KEYCLOAK_AUTH_SERVER=http://keycloak:8080
KEYCLOAK_JWK_URI=http://keycloak:8080/realms/spring/protocol/openid-connect/certs
KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/spring

# Eureka
EUREKA_URL=http://eureka:8761/eureka/

# RabbitMQ
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=admin123
```

#### 📁 Frontend `.env` — place inside the `frontend/` folder

```env
VITE_API_URL=http://localhost:9000

VITE_KEYCLOAK_URL=http://localhost:8080
VITE_KEYCLOAK_REALM=spring
VITE_KEYCLOAK_CLIENT_ID=frontend
```

---

### 2. Build and Run

Once both `.env` files are in place, run the following command from the **project root**:

```bash
docker compose up --build
```

This will build all service images and bring up the entire stack. Wait until all services are healthy before proceeding to the Keycloak configuration step.

---

### 3. Configure Keycloak

After the stack is running, open the Keycloak Admin Console:

```
http://localhost:8080
```

Login with:
- **Username:** `admin`
- **Password:** `admin`

---

#### 3.1 Create the Realm

1. In the top-left dropdown, click **"Create Realm"**
2. Set the **Realm name** to exactly: `spring`
3. Click **"Create"**

---

#### 3.2 Create Realm Roles

Inside the `spring` realm, navigate to **Realm roles → Create role** and create the following three roles one by one:

| Role Name | Description |
|---|---|
| `ADMIN` | Full system access, including deletion of cases and evidence |
| `DETECTIVE` | Can manage and work on active cases |
| `VIEWER` | Read-only access to the system |

---

#### 3.3 Create Clients

Inside the `spring` realm, go to **Clients → Create client** and create the following three clients:

> ⚠️ **Important:** When creating each client, **only enable Standard flow**. All other authentication flows (Direct access grants, Implicit flow, Service accounts, etc.) must remain **disabled**.

---

**Client 1: `frontend`**

| Field | Value |
|---|---|
| Client ID | `frontend` |
| Client type | `OpenID Connect` |
| Standard flow | ✅ Enabled |
| All other flows | ❌ Disabled |
| **PKCE Method** | **`S256`** |
| Root URL | `http://localhost:5173` |
| Home URL | `http://localhost:5173/dashboard` |
| Valid redirect URIs | `http://localhost:5173/callback` |
| Valid post logout redirect URIs | `http://localhost:5173/*` |
| Web origins | `http://localhost:5173` |
| Admin URL | `http://localhost:5173` |

> 🔒 **PKCE (Proof Key for Code Exchange):** The **PKCE Method** field appears directly in the **Capability config** step during client creation. Set it to `S256` before saving. This is required for secure public client authentication from the browser.

---

**Client 2: `api-gateway`**

| Field | Value |
|---|---|
| Client ID | `api-gateway` |
| Client type | `OpenID Connect` |
| Standard flow | ✅ Enabled |
| All other flows | ❌ Disabled |
| Root URL | `http://localhost:9000` |
| Valid redirect URIs | `http://localhost:9000/*` |
| Web origins | `http://localhost:9000` |

---

**Client 3: `microservices`**

| Field | Value |
|---|---|
| Client ID | `microservices` |
| Client type | `OpenID Connect` |
| Standard flow | ✅ Enabled |
| All other flows | ❌ Disabled |

---

### 4. Access the Application

| Service | URL |
|---|---|
| Frontend | http://localhost:5173 |
| API Gateway | http://localhost:9000 |
| Keycloak Admin Console | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |

---

## 📁 Project Structure

```
.
├── docker-compose.yml
├── .env                    ← Root environment file (you must create this)
├── frontend/
│   ├── .env                ← Frontend environment file (you must create this)
│   └── ...
├── api-gateway/
├── case-service/
├── people-service/
├── evidence-service/
└── ...
```

---

## 📋 Business Rules Summary

- A case can have **many evidences** and **many tasks**
- Evidence must maintain a full **chain of custody** history
- Tasks can only be assigned to users with the `DETECTIVE` role
- Only `ADMIN` users can **delete** cases or evidence
- All major system events are published for audit and notification purposes

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Vite + React |
| API Gateway | Spring Cloud Gateway |
| Microservices | Spring Boot |
| Authentication | Keycloak |
| Service Discovery | Netflix Eureka |
| Database | PostgreSQL |
| Containerization | Docker / Docker Compose |

---

## Testing Strategy

### Objective

The test suite validates the correctness of the business logic, the interaction between application layers, and the real HTTP behavior of each microservice's API. Tests are organized into three levels — unit, integration, and end-to-end — following the Testing Pyramid approach.

---

### Test Classification

Each of the six business microservices (case-service, evidence-service, people-service, workflow-service, audit-service, notification-service) contains three types of tests:

| Type | Framework | Scope |
|---|---|---|
| **Unit** | JUnit 5 + Mockito | Service layer in isolation — dependencies mocked |
| **Integration** | Spring `@WebMvcTest` + MockMvc | Controller + Service wired together, no real HTTP |
| **E2E** | Spring `@SpringBootTest` + `TestRestTemplate` | Full application stack, real HTTP on a random port |

#### Unit Tests — `unit/`

Each `XxxServiceTest` class covers the service methods in complete isolation:

| Service | Test class | Scenarios covered |
|---|---|---|
| Case | `CaseServiceTest` | `findAll` returns list / empty list; `findById` found / not found; `create` saves and publishes event; `updateById` updates and returns; `deleteById` deletes / throws when not found |
| Evidence | `EvidenceServiceTest` | `findAll`; `findById` found / not found; `findByCaseId`; `create` saves, records initial custody, publishes event; `updateById` updates custody history on custodian change; `deleteById` |
| People | `PersonServiceTest` | `findAll`; `findById` found / not found; `create`; `updateById`; `deleteById` |
| Workflow | `TaskServiceTest` | `findAll`; `findById`; `create` with and without assignee; `updateById` re-assigns; `deleteById`; overdue check publishes event and marks status |
| Audit | `AuditLogServiceTest` | `findAll`; `save` persists correctly |
| Notification | `NotificationServiceTest` | `findAll`; `save` persists correctly |

#### Integration Tests — `integration/`

Each `XxxControllerTest` uses `@WebMvcTest` and `MockMvc` to test the full controller-to-service pipeline without starting a real server or connecting to a database. Keycloak JWT validation is disabled via the test profile.

| Service | Test class | Scenarios covered |
|---|---|---|
| Case | `CaseControllerTest` | `GET /cases` 200; `GET /cases/{id}` 200 / 404; `POST /cases` 201; `PUT /cases/{id}` 200; `DELETE /cases/{id}` 204 |
| Evidence | `EvidenceControllerTest` | `GET /evidences`; `GET /evidences/{id}`; `GET /evidences/case/{caseId}`; `GET /evidences/{id}/custody`; `POST /evidences` 201; `PUT /evidences/{id}`; `DELETE /evidences/{id}` |
| People | `PersonControllerTest` | `GET /people`; `GET /people/{id}`; `POST /people` 201; `PUT /people/{id}`; `DELETE /people/{id}` |
| Workflow | `TaskControllerTest` | `GET /tasks`; `GET /tasks/{id}`; `GET /tasks/case/{caseId}`; `POST /tasks` 201; `PUT /tasks/{id}`; `DELETE /tasks/{id}` |
| Audit | `AuditLogControllerTest` | `GET /audit-logs`; `GET /audit-logs/{id}` |
| Notification | `NotificationControllerTest` | `GET /notifications`; `GET /notifications/{id}` |

#### E2E Tests — `e2e/`

Each `XxxE2ETest` boots the full Spring context on a random port using `@SpringBootTest(webEnvironment = RANDOM_PORT)` and sends real HTTP requests via `TestRestTemplate`. An H2 in-memory database (PostgreSQL compatibility mode) is used instead of the real PostgreSQL instance.

| Service | Test class | Scenarios covered |
|---|---|---|
| Case | `CaseE2ETest` | Create case → 201; retrieve by ID → 200; list all → 200; update → 200; delete → 204; retrieve deleted → 404 |
| Evidence | `EvidenceE2ETest` | Create evidence → 201; retrieve by ID → 200; get custody history → 200; update custodian → records new history entry; delete → 204 |
| People | `PersonE2ETest` | Create person → 201; retrieve by ID → 200; update → 200; delete → 204; retrieve deleted → 404 |
| Workflow | `TaskE2ETest` | Create task → 201; retrieve by ID → 200; list by caseId → 200; update → 200; delete → 204 |
| Audit | `AuditLogE2ETest` | List audit logs → 200; retrieve by ID → 200 |
| Notification | `NotificationE2ETest` | List notifications → 200; retrieve by ID → 200 |

---

### Test Environment Configuration

All services share the same test profile pattern. The file `src/test/resources/application-test.yml` is activated via `@ActiveProfiles("test")` and configures:

- **H2 in-memory database** (PostgreSQL compatibility mode) — no external database required
- **Spring Cloud Config disabled** — `spring.cloud.config.enabled: false`
- **Keycloak JWT disabled** — `jwk-set-uri` points to a non-existent local address so security filters are bypassed by test configuration
- **DDL auto: create-drop** — schema is recreated for each test run

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect
  cloud:
    config:
      enabled: false
  config:
    import: ""
```

---

### How to Run Tests Locally

#### Prerequisites

- Java 21+
- Maven 3.8+
- No running Docker/PostgreSQL required (tests use H2)

#### Run all tests for a single service

```bash
mvn test -pl apps/case-service
mvn test -pl apps/evidence-service
mvn test -pl apps/people-service
mvn test -pl apps/workflow-service
mvn test -pl apps/audit-service
mvn test -pl apps/notification-service
```

#### Run all backend tests at once

```bash
mvn test --projects apps/case-service,apps/evidence-service,apps/people-service,apps/workflow-service,apps/audit-service,apps/notification-service
```

#### Run only unit tests

```bash
mvn test -pl apps/case-service -Dtest="**/unit/**"
```

#### Run only integration tests

```bash
mvn test -pl apps/case-service -Dtest="**/integration/**"
```

#### Run only E2E tests

```bash
mvn test -pl apps/case-service -Dtest="**/e2e/**"
```

---

### Conclusions

| Aspect | Assessment |
|---|---|
| **Layer coverage** | All three testing layers (unit, integration, E2E) are implemented for each of the six business services |
| **Business rule coverage** | Key rules are exercised: ADMIN-only delete, chain of custody recording on custodian change, default task creation on CaseCreated, overdue task detection |
| **Event-driven coverage** | Unit tests verify event publishing via mocked `RabbitTemplate`; integration tests mock the publisher component |
| **Error path coverage** | Not-found exceptions (404), validation errors (400), and unauthorized access are covered in both integration and E2E tests |
| **Test isolation** | Each test is fully isolated — H2 resets between runs, no shared state between test classes |
| **Improvement areas** | RabbitMQ consumer logic (listeners) could benefit from dedicated unit tests using embedded broker; contract tests between services would strengthen the event-driven guarantees |