import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { CircularProgress, Box } from '@mui/material';
import { AccessDenied } from '../AccessDenied/AccessDenied';

export const LOGIN_PATH = "/login";

interface RoleProtectedRouteProps {
  children: React.ReactNode;
  requiredRole: string;
}

export const RoleProtectedRoute: React.FC<RoleProtectedRouteProps> = ({ 
  children, 
  requiredRole 
}) => {
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
