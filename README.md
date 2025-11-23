# Optional Subjects Selection Platform (OSSP)

A platform where students can submit their optional course preferences and administrators can manage allocations.

## 📋 Prerequisites

- **Java 21+**
- **Maven 3.6+**
- **Node.js 18+** and **npm**
- **PostgreSQL 12+**

## 🚀 First-Time Setup

### 1. Clone the Repository
```bash
git clone https://github.com/TeodoraVlad12/MISS-Year1-Team17-OptionalSubjectsSelectionPlatform.git
cd MISS-Year1-Team17-OptionalSubjectsSelectionPlatform
```

### 2. Install PostgreSQL

#### On macOS:
```bash
brew install postgresql
```

#### On Windows:
- Download installer from [postgresql.org/download/windows](https://www.postgresql.org/download/windows/)
- Or use Chocolatey: `choco install postgresql`

#### On Linux:
```bash
sudo apt-get install postgresql postgresql-contrib
```

### 3. Create Database (One-Time Setup)

#### Start PostgreSQL Service:

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

#### Create Database and User:
```bash
# Connect to PostgreSQL
psql postgres

# Run these SQL commands:
CREATE DATABASE ossp_db;
CREATE USER ossp_user WITH PASSWORD 'ossp_password';
GRANT ALL PRIVILEGES ON DATABASE ossp_db TO ossp_user;
\q
```

### 4. Install Frontend Dependencies
```bash
cd frontend
npm install
cd ..
```

## 🔄 Running the Application (Every Time)

### 1. Start PostgreSQL Service

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
```

### 2. Start the Backend
```bash
cd backend
mvn spring-boot:run
```
Backend will start on: `http://localhost:8080`

### 3. Start the Frontend (in a new terminal)
```bash
cd frontend
npm run dev
```
Frontend will start on: `http://localhost:5173`

## 🗄️ Database Information

- **Database Name:** `ossp_db`
- **Username:** `ossp_user`
- **Password:** `ossp_password`
- **Host:** `localhost`
- **Port:** `5432`

The application uses a consolidated User entity that replaces separate Student and Secretary tables, providing cleaner relationships and better performance.

## 🧪 Testing the Application

### Test Login Credentials:
- **Student:** `student@student.uaic.ro` (any password)
- **Admin:** `admin@uaic.ro` (any password)

The application uses mock authentication for development purposes.

## 🛠️ Development Features

- **H2 Console:** Available for development (commented out in favor of PostgreSQL)
- **Hot Reload:** Backend supports Spring Boot DevTools
- **Live Reload:** Frontend supports Vite hot module replacement
- **JWT Authentication:** Secure token-based authentication
- **Role-Based Access:** Different interfaces for students and administrators

## 📁 Project Structure

```
├── backend/                 # Spring Boot backend
│   ├── src/main/java/      # Java source code
│   └── src/main/resources/ # Configuration files
├── frontend/               # React TypeScript frontend
│   ├── src/components/     # React components
│   └── src/services/       # API services
└── docs/                   # Documentation and diagrams
```

## 🔧 Troubleshooting

### Backend won't start:
- Ensure PostgreSQL is running
- Verify database `ossp_db` and user `ossp_user` exist
- Check if port 8080 is available

### Frontend won't start:
- Run `npm install` in the frontend directory
- Check if port 5173 is available
- Ensure Node.js 18+ is installed

### Database connection issues:
- Verify PostgreSQL service is running
- Check database credentials in `backend/src/main/resources/application.properties`

## 🏗️ Architecture

The application uses a consolidated database design with:
- **Single User Entity:** Replaces separate Student/Secretary tables
- **Clean Relationships:** All entities reference the unified User model
- **Production Ready:** PostgreSQL database for reliable data persistence
- **Modern Stack:** Spring Boot 3.5 + React 18 + TypeScript

---

For more information, check the `docs/` directory for architectural diagrams and detailed documentation.
