# Installation Guide

Event registration web application. Administrators can create events; anyone can register for them.

The application consists of:
- **Web server** – Spring Boot (Java 21), port 8080
- **Database** – PostgreSQL 16
- **Frontend** – React (served from the web server)

Database tables are created automatically on first startup.

---

## Option A: Docker Compose (recommended)

### Requirements

- Linux server (Ubuntu 22.04+ recommended) or Windows Server with WSL2
- [Docker Engine 24+](https://docs.docker.com/engine/install/) and Docker Compose v2
- Port 8080 available
- At least 1 GB RAM, 1 CPU

### Steps

**1. Copy the project files to the server**

```bash
git clone <repository-url>
cd events-app
```

Or download and extract the ZIP archive.

**2. Set admin credentials**

```bash
cp .env.example .env
```

Open `.env` and change the default values:

```
ADMIN_EMAIL=admin@yourdomain.com
ADMIN_PASSWORD=strong-password-here
```

> Do not use the default password (`changeme`) in production.

**3. Start the application**

```bash
docker compose up -d --build
```

The first start takes longer (around 3–5 minutes) while Docker builds the image. Subsequent starts are faster.

**4. Verify the application is running**

Open in a browser: `http://<server-ip>:8080`

The events list should be visible. To log in as admin, click the **Admin** button in the top-right corner.

---

### Management

| Action | Command |
|--------|---------|
| Stop | `docker compose down` |
| View logs | `docker compose logs -f app` |
| Restart | `docker compose restart app` |
| Update | `git pull && docker compose up -d --build` |
| Stop and delete data | `docker compose down -v` |

---

## Option B: Manual installation

### Requirements

- Linux (Ubuntu 22.04+) or Windows
- [Java 21 JDK](https://adoptium.net/) (Temurin recommended)
- [Node.js 20+](https://nodejs.org/)
- [PostgreSQL 16](https://www.postgresql.org/download/)
- Port 8080 available

### Steps

**1. Copy the project files to the server**

```bash
git clone <repository-url>
cd events-app
```

**2. Create the database**

Connect to PostgreSQL and run:

```sql
CREATE USER events WITH PASSWORD 'strong-db-password';
CREATE DATABASE events OWNER events;
```

**3. Build the frontend**

```bash
cd frontend
npm ci
npm run build
cd ..
```

**4. Copy the frontend into the static resources folder**

Linux / macOS:
```bash
mkdir -p src/main/resources/static
cp -r frontend/dist/* src/main/resources/static/
```

Windows (PowerShell):
```powershell
New-Item -ItemType Directory -Force -Path src\main\resources\static
Copy-Item -Recurse frontend\dist\* src\main\resources\static\
```

**5. Build the application**

```bash
./gradlew bootJar
```

This produces `build/libs/events-app-0.0.1-SNAPSHOT.jar`.

**6. Start the application**

Linux / macOS:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/events
export DB_USER=events
export DB_PASSWORD=strong-db-password
export ADMIN_EMAIL=admin@yourdomain.com
export ADMIN_PASSWORD=strong-admin-password

java -jar build/libs/events-app-0.0.1-SNAPSHOT.jar
```

Windows (PowerShell):
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/events"
$env:DB_USER="events"
$env:DB_PASSWORD="strong-db-password"
$env:ADMIN_EMAIL="admin@yourdomain.com"
$env:ADMIN_PASSWORD="strong-admin-password"

java -jar build\libs\events-app-0.0.1-SNAPSHOT.jar
```

**7. Verify the application is running**

Open in a browser: `http://localhost:8080`

---

### Running as a background service (Linux, optional)

Create a systemd unit file at `/etc/systemd/system/events-app.service`:

```ini
[Unit]
Description=Events App
After=network.target postgresql.service

[Service]
User=events
WorkingDirectory=/opt/events-app
Environment="DB_URL=jdbc:postgresql://localhost:5432/events"
Environment="DB_USER=events"
Environment="DB_PASSWORD=strong-db-password"
Environment="ADMIN_EMAIL=admin@yourdomain.com"
Environment="ADMIN_PASSWORD=strong-admin-password"
ExecStart=java -jar /opt/events-app/events-app.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable events-app
sudo systemctl start events-app
```

---

## Troubleshooting

**Port 8080 is already in use**
```
Web server failed to start. Port 8080 was already in use.
```
Free the port or change `server.port` in `application.yml`.

**Database connection refused**
```
Connection refused: localhost:5432
```
Check that PostgreSQL is running and credentials are correct.

**Wrong Java version**
```
UnsupportedClassVersionError
```
Run `java -version` — the output must start with `21`. Install [Java 21](https://adoptium.net/).

**Docker build fails**
Check that the Docker daemon is running: `docker info`. On Windows, make sure Docker Desktop is started.
