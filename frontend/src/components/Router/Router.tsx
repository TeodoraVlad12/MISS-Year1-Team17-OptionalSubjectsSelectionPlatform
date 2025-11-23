import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import type { Route } from './Router.types';
import { ErrorBoundary } from '../ErrorBoundary/ErrorBoundary';
import { AdminAssignOptionalsPage } from '../AdminAssignOptionalsPage/AdminAssignOptionalsPage';
import Login from '../Login/Login';
import Dashboard from '../Dashboard/Dashboard';
import { RouteError } from '../RouteError/RouteError';
import { useAuth } from '../../contexts/AuthContext';
import { CircularProgress, Box, Card, CardContent, Typography, Button, Alert } from '@mui/material';
import { Block as BlockIcon, Home as HomeIcon } from '@mui/icons-material';

export const ASSIGN_OPTIONALS_PATH = "/optionals/assign";
export const LOGIN_PATH = "/login";
export const DASHBOARD_PATH = "/dashboard";

// Protected Route Component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    return <Navigate to={LOGIN_PATH} replace />;
  }

  return <>{children}</>;
};

// Role-Based Protected Route Component
const RoleProtectedRoute: React.FC<{ 
  children: React.ReactNode;
  requiredRole: string;
}> = ({ children, requiredRole }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (!user) {
    return <Navigate to={LOGIN_PATH} replace />;
  }

  if (user.role !== requiredRole) {
    return <AccessDenied userRole={user.role} requiredRole={requiredRole} />;
  }

  return <>{children}</>;
};

// Access Denied Component
const AccessDenied: React.FC<{ userRole: string; requiredRole: string }> = ({ userRole, requiredRole }) => {
  return (
    <Box 
      display="flex" 
      justifyContent="center" 
      alignItems="center" 
      minHeight="100vh"
      p={3}
    >
      <Card sx={{ maxWidth: 500, textAlign: 'center' }}>
        <CardContent sx={{ p: 4 }}>
          <BlockIcon sx={{ fontSize: 64, color: 'error.main', mb: 2 }} />
          
          <Typography variant="h4" component="h1" gutterBottom>
            Access Denied
          </Typography>
          
          <Alert severity="error" sx={{ mb: 3 }}>
            You don't have permission to access this page.
          </Alert>
          
          <Button
            variant="contained"
            startIcon={<HomeIcon />}
            onClick={() => window.location.href = DASHBOARD_PATH}
            size="large"
          >
            Return to Dashboard
          </Button>
        </CardContent>
      </Card>
    </Box>
  );
};

// Public Route Component (redirects to dashboard if logged in)
const PublicRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (user) {
    return <Navigate to={DASHBOARD_PATH} replace />;
  }

  return <>{children}</>;
};

const routes: Route[] = [
    {
        path: "/",
        element: <Navigate to={DASHBOARD_PATH} replace />,
    },
    {
        path: LOGIN_PATH,
        element: (
          <PublicRoute>
            <Login />
          </PublicRoute>
        ),
    },
    {
        path: DASHBOARD_PATH,
        element: (
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        ),
    },
    {
        path: ASSIGN_OPTIONALS_PATH,
        element: (
          <RoleProtectedRoute requiredRole="ADMIN">
            <AdminAssignOptionalsPage />
          </RoleProtectedRoute>
        ),
    },
];

export const Router = () => {
    const router = createBrowserRouter(
        routes.map((route) => ({
            path: route.path,
            element: <ErrorBoundary>{route.element}</ErrorBoundary>,
            errorElement: route.errorElement || <RouteError />,
        }))
    );

    return <RouterProvider router={router} />;
};
