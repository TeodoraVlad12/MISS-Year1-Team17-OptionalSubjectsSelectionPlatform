# React + TypeScript + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

## Running the Project

To run first time:
```bash
npm install && npm run dev
```

To run:
```bash
npm run dev
```

## Project Structure

```
src/
├── assets/          # Static assets like images, icons, and SVG files
├── components/      # React components (each component has its own folder)
├── library/         # Common logic and utility functions unrelated to business logic
├── models/          # Business-specific entity models and interfaces
├── services/        # API services for backend communication, contexts, hooks, and state management
├── index.css        # Global styles
└── main.tsx         # Application entry point
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
