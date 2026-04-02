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