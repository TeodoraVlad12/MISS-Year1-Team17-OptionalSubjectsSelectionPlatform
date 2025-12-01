import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import type { Route } from './Router.types';
import { ErrorBoundary } from '../ErrorBoundary/ErrorBoundary';
import { AdminAssignOptionalsPage } from '../AdminAssignOptionalsPage/AdminAssignOptionalsPage';
import Login from '../Login/Login';
import Dashboard from '../Dashboard/Dashboard';
import StudentPreferences from '../StudentPreferences/StudentPreferences';
import { RouteError } from '../RouteError/RouteError';
import { ProtectedRoute } from '../ProtectedRoute/ProtectedRoute';
import { RoleProtectedRoute } from '../RoleProtectedRoute/RoleProtectedRoute';
import { PublicRoute } from '../PublicRoute/PublicRoute';

export const ASSIGN_OPTIONALS_PATH = "/optionals/assign";
export const STUDENT_PREFERENCES_PATH = "/preferences";
export const LOGIN_PATH = "/login";
export const DASHBOARD_PATH = "/dashboard";

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
    {
        path: STUDENT_PREFERENCES_PATH,
        element: (
          <RoleProtectedRoute requiredRole="STUDENT">
            <StudentPreferences />
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
