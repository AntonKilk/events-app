# Events App

A simple event registration service. Administrators create events; anyone can register for them.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose
- [JDK 21](https://adoptium.net/) (for running the backend locally)
- [Node.js 20+](https://nodejs.org/) (for running the frontend locally)

---

## Running with Docker Compose

The easiest way to run the full stack.

**1. Configure admin credentials**

Copy `.env.example` to `.env` and edit it:

```bash
cp .env.example .env
```

```
ADMIN_EMAIL=admin@example.com
ADMIN_PASSWORD=changeme
```

**2. Start the database**

```bash
docker compose up -d db
```

**3. Start the backend**

```bash
./gradlew bootRun
```

The API starts at `http://localhost:8080`. Liquibase runs the database migrations automatically on first start.

**4. Start the frontend**

```bash
cd frontend
npm install
npm run dev
```

The app is available at `http://localhost:5173`.

---

## Running fully in Docker

Build and run everything in containers:

```bash
cp .env.example .env   # edit credentials first
docker compose up --build
```

The app is available at `http://localhost:8080`.

---

## Admin access

Log in at the top-right corner of the page using the credentials from `.env`.  
The default credentials (if `.env` is not configured) are `admin@example.com` / `changeme`.

---

## Project structure

```
.
├── src/                  # Spring Boot backend (Java 21)
│   └── main/
│       ├── java/         # Application code
│       └── resources/
│           ├── application.yml          # App configuration
│           └── db/changelog/changes/    # Liquibase migrations
├── frontend/             # React + TypeScript + Tailwind frontend
│   └── src/
├── docker-compose.yml    # PostgreSQL + app services
└── .env                  # Admin credentials (gitignored)
```
