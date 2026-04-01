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
| Backend | Java 25 + Spring Boot 4.0 |
| Database | PostgreSQL 16 |

## Prerequisites

- Java 25+
- Maven 3.9+
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

The backend starts on `http://localhost:8080`.

> **Note:** The database runs on port **5433** (not the default 5432) to avoid conflicts with other local PostgreSQL instances.

## Running tests

**Backend unit tests** (no Docker required)

```bash
cd backend
mvn test
```


## Project structure

```
managertools/
├── docker-compose.yml       # Local PostgreSQL
├── backend/                 # Spring Boot API
└── frontend/                # 
```
