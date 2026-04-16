# Events App

A simple event registration service. Administrators create events; anyone can register for them.

<img width="995" height="516" alt="view" src="https://github.com/user-attachments/assets/b086a0e9-d8e5-4d0d-9870-51c00bf3fc06" />


## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and Docker Compose
- [JDK 21](https://adoptium.net/) (for running the backend locally)
- [Node.js 20+](https://nodejs.org/) (for running the frontend locally)

---

## Configuration

Admin credentials are set in `src/main/resources/application-local.yml`:

```yaml
app:
  admin:
    email: admin@example.com
    password: changeme
```

Edit this file before starting the app.

---

## Running locally

**1. Start the database**

The backend requires PostgreSQL to be running before it starts.

```bash
docker compose up -d db
```

Wait a few seconds, then verify the `db` service shows `healthy`:

```bash
docker compose ps
```

**2. Start the backend**

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

The API starts at `http://localhost:8080`. Liquibase runs the database migrations automatically on first start.

> **Tip:** To build the JAR without running tests (tests spin up Docker containers and take several minutes), use `./gradlew bootJar -x test`.

**3. Start the frontend**

```bash
cd frontend
npm install
npm run dev
```

The app is available at `http://localhost:5173`.

---

## Running fully in Docker

Builds and starts everything in one command:

```bash
docker compose up --build
```

The app is available at `http://localhost:8080`.

---

## Admin access

Log in at the top-right corner of the page using the credentials from `application-local.yml`.

---

## Project structure

```
.
├── src/                  # Spring Boot backend (Java 21)
│   └── main/
│       ├── java/         # Application code
│       └── resources/
│           ├── application.yml          # App configuration
│           ├── application-local.yml    # Local credentials (edit this)
│           └── db/changelog/changes/    # Liquibase migrations
├── frontend/             # React + TypeScript + Tailwind frontend
│   └── src/
└── docker-compose.yml    # PostgreSQL + app services
```
