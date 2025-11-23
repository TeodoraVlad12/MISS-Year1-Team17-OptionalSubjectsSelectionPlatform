import React from 'react';
import { Box, Card, CardContent, Typography, Button, Alert } from '@mui/material';
import { Block as BlockIcon, Home as HomeIcon } from '@mui/icons-material';

export const DASHBOARD_PATH = "/dashboard";

interface AccessDeniedProps {
  userRole: string;
  requiredRole: string;
}

export const AccessDenied: React.FC<AccessDeniedProps> = ({ userRole, requiredRole }) => {
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
            You don't have permission to access this page. Required role: {requiredRole}, your role: {userRole}.
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
