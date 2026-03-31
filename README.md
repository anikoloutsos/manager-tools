# Chapter Manager

A personal tool for chapter leads to manage 1:1s, keep private notes on each engineer, and prepare for meetings. Built for local use — no auth, no cloud, your data stays on your machine.

## Features

- **Engineer list** — overview of your whole team with last note date at a glance
- **Per-engineer notes** — create, edit, delete, and search notes tied to each 1:1
- **Pre-meeting summary** — one-click digest of the last 5 notes before a meeting
- **Full-text search** — find past decisions and topics across an engineer's note history

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 24 + Spring Boot 3.4 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA + Hibernate |
| Frontend | React 19 + TypeScript + Vite 8 |
| Styling | Tailwind CSS |
| HTTP client | Axios + TanStack Query |
| Routing | React Router v6 |

## Prerequisites

- Java 24+
- Maven 3.9+
- Node.js 20+
- Docker (for PostgreSQL)

## Running locally

**1. Start the database**

```bash
docker-compose up -d
```

**2. Start the backend**

```bash
cd backend
mvn spring-boot:run
```

The backend starts on `http://localhost:8080`. On the first run it seeds 12 placeholder engineers into the database.

> **Note:** The database runs on port **5433** (not the default 5432) to avoid conflicts with other local PostgreSQL instances.

**3. Start the frontend**

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173` in your browser.

## Running tests

**Backend unit tests** (no Docker required)

```bash
cd backend
mvn test
```

**Backend integration tests** (requires Docker — spins up a real PostgreSQL container via Testcontainers)

```bash
cd backend
mvn verify
```

**Frontend tests**

```bash
cd frontend
npm test
```

## Project structure

```
managertools/
├── docker-compose.yml       # Local PostgreSQL
├── backend/                 # Spring Boot API
│   └── src/
│       ├── main/java/com/managertools/
│       │   ├── engineer/    # Engineer CRUD
│       │   └── note/        # Note CRUD, search, summary
│       └── test/            # Testcontainers integration tests + Mockito unit tests
└── frontend/                # React + TypeScript SPA
    └── src/
        ├── api/             # Typed API client
        ├── pages/           # Dashboard, EngineerDetail, NoteEditor, PreMeetingSummary
        └── test/            # MSW handlers and Vitest setup
```

## Roadmap

- **V2** — Goals and OKR tracking (per-engineer, biweekly check-ins, 6-month review cycles)
- **Later** — Chapter day planning and learning session tracking
