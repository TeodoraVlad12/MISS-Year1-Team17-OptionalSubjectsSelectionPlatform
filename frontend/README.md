# Optional Subjects Selection Platform - Frontend

##  First-Time Setup

### 1. Install Dependencies

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install
```

##  Running the Frontend (Every Time)

### 1. Start the Development Server

```bash
# Navigate to frontend directory
cd frontend

# Start the development server
npm run dev
```

The frontend will start on: `http://localhost:5173`

##  Login Requirements

The application has **specific email format requirements** for authentication:

###  Student Login
- **Email Format:** Must end with `@student.uaic.ro`
- **Example:** `john.doe@student.uaic.ro`
- **Password:** Any password (development mode)
- **Access:** Student dashboard with course preferences

###  Admin Login  
- **Email Format:** Must end with `@uaic.ro`
- **Example:** `admin@uaic.ro`
- **Password:** Any password (development mode)
- **Access:** Admin dashboard with allocation management

###  Invalid Login Formats
- `user@gmail.com` -  Not accepted
- `student@uaic.com` -  Wrong domain  
- `admin@student.uaic.ro` -  Admin using student domain

## Test Accounts

### Ready-to-Use Test Accounts:

**Students:**
- `john.doe@student.uaic.ro`
- `jane.smith@student.uaic.ro`
- `student@student.uaic.ro`

**Admins:**
- `admin@uaic.ro`
- `secretary@uaic.ro`
- `coordinator@uaic.ro`

**Note:** Any password works in development mode.


##  Project Structure

```
frontend/
├── src/
│   ├── components/           # React components
│   │   ├── App/             # Main application component
│   │   ├── Login/           # Authentication component
│   │   ├── Dashboard/       # Dashboard components
│   │   └── Router/          # Routing configuration
│   ├── contexts/            # React contexts (Auth)
│   ├── services/            # API services
│   ├── models/              # TypeScript interfaces
│   └── library/             # Constants and utilities
├── public/                  # Static assets
└── package.json            # Dependencies and scripts
```

### Components Structure

Each component should be organized in its own folder with the following structure:

```
ComponentName/
├── ComponentName.tsx          # Component implementation
├── ComponentName.styles.ts    # Styled components or CSS-in-JS (or .css/.scss)
└── ComponentName.types.ts     # TypeScript types and interfaces (e.g., component props)
```

### Folder Descriptions

- **assets/**: Contains static assets like images, icons, and SVG files used throughout the application.
- **components/**: Contains all React components, each in its own folder with implementation, styles, and type definitions.
- **library/**: Houses common utility functions and logic that are framework-agnostic and unrelated to business logic.
- **models/**: Defines business-specific entity models, interfaces, and domain objects.
- **services/**: Contains API services for backend communication, React contexts, custom hooks, and other service-layer code.
