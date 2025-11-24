# Optional Subjects Selection Platform - Backend

##  First-Time Setup

### 1. Install PostgreSQL

#### On macOS:
```bash
brew install postgresql
```

#### On Windows:
- Download installer from [postgresql.org/download/windows](https://www.postgresql.org/download/windows/)
- Or use Chocolatey: `choco install postgresql`


### 2. Start PostgreSQL Service

**macOS:**
```bash
brew services start postgresql
```

**Windows (Command Prompt as Administrator):**
```cmd
net start postgresql-x64-15
```

**Linux:**
```bash
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 3. Create Database and User (One-Time Setup)

```bash
# Connect to PostgreSQL
psql postgres

# Run these SQL commands:
CREATE DATABASE ossp_db;
CREATE USER ossp_user WITH PASSWORD 'ossp_password';
GRANT ALL PRIVILEGES ON DATABASE ossp_db TO ossp_user;
\q
```

##  Running the Backend (Every Time)

### 1. Start PostgreSQL Service (if not already running)

**macOS:**
```bash
brew services start postgresql
```

**Windows:**
```cmd
net start postgresql-x64-15
```

**Linux:**
```bash
sudo systemctl start postgresql
```

### 2. Run the Application

```bash
# Navigate to backend directory
cd backend

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on: `http://localhost:8080`

##  Database Configuration

- **Database Name:** `ossp_db`
- **Username:** `ossp_user`
- **Password:** `ossp_password`
- **Host:** `localhost`
- **Port:** `5432`

Configuration is located in `src/main/resources/application.properties`.


