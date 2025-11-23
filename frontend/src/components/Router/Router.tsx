import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import type { Route } from './Router.types';
import { ErrorBoundary } from '../ErrorBoundary/ErrorBoundary';
import { AdminAssignOptionalsPage } from '../AdminAssignOptionalsPage/AdminAssignOptionalsPage';
import { RouteError } from '../RouteError/RouteError';

export const ASSIGN_OPTIONALS_PATH = "/optionals/assign";

const routes: Route[] = [
    {
        path: ASSIGN_OPTIONALS_PATH,
        element: <AdminAssignOptionalsPage />,
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
