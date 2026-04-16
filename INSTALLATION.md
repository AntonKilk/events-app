# Manual Installation Guide

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

**2. Set admin credentials**

Open `src/main/resources/application-local.yml` and change the default values:

```yaml
app:
  admin:
    email: admin@yourdomain.com
    password: strong-password-here
```

**3. Create the database**

Connect to PostgreSQL and run:

```sql
CREATE USER events WITH PASSWORD 'strong-db-password';
CREATE DATABASE events OWNER events;
```

**4. Build the frontend**

```bash
cd frontend
npm ci
npm run build
cd ..
```

**5. Copy the frontend into the static resources folder**

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

**6. Build the application**

```bash
./gradlew bootJar -x test
```

This produces `build/libs/events-app-0.0.1-SNAPSHOT.jar`.

**7. Start the application**

Linux / macOS:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/events
export DB_USER=events
export DB_PASSWORD=strong-db-password

java -jar build/libs/events-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Windows (PowerShell):
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/events"
$env:DB_USER="events"
$env:DB_PASSWORD="strong-db-password"

java -jar build\libs\events-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

**8. Verify the application is running**

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
ExecStart=java -jar /opt/events-app/events-app.jar --spring.profiles.active=local
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
