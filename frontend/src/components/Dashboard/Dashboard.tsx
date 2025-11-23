import React from 'react';
import {
  Typography,
  Box,
  Card,
  CardContent,
  Button,
  Chip
} from '@mui/material';
import { School, AdminPanelSettings } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import TopBar from '../TopBar/TopBar';

const Dashboard: React.FC = () => {
  const { user: userInfo, logout } = useAuth();

  if (!userInfo) {
    return null; // This shouldn't happen due to protected routes, but just in case
  }

  const handleLogout = () => {
    logout();
  };

  const getRoleIcon = () => {
    return userInfo.role === 'STUDENT' ? <School /> : <AdminPanelSettings />;
  };

  const getRoleColor = () => {
    return userInfo.role === 'STUDENT' ? 'primary' : 'secondary';
  };

  return (
    <Box>
      <TopBar 
        title="ElectiveMatch - Optional Subjects Selection"
        showRoleChip={true}
      />

      <Box p={3}>
        <Typography variant="h4" gutterBottom>
          Welcome, {userInfo.firstName}!
        </Typography>

        <Card sx={{ mb: 3 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              User Information
            </Typography>
            
            <Box display="flex" flexWrap="wrap" gap={2}>
              <Box>
                <Typography variant="subtitle2" color="textSecondary">
                  Email
                </Typography>
                <Typography variant="body1">
                  {userInfo.email}
                </Typography>
              </Box>

              <Box>
                <Typography variant="subtitle2" color="textSecondary">
                  Role
                </Typography>
                <Chip 
                  icon={getRoleIcon()} 
                  label={userInfo.role}
                  color={getRoleColor() as any}
                  size="small"
                />
              </Box>

              {userInfo.matriculationNumber && (
                <Box>
                  <Typography variant="subtitle2" color="textSecondary">
                    Matriculation Number
                  </Typography>
                  <Typography variant="body1">
                    {userInfo.matriculationNumber}
                  </Typography>
                </Box>
              )}

              {userInfo.academicYear && (
                <Box>
                  <Typography variant="subtitle2" color="textSecondary">
                    Academic Year
                  </Typography>
                  <Typography variant="body1">
                    Year {userInfo.academicYear}
                  </Typography>
                </Box>
              )}

              {userInfo.specialization && (
                <Box>
                  <Typography variant="subtitle2" color="textSecondary">
                    Specialization
                  </Typography>
                  <Typography variant="body1">
                    {userInfo.specialization}
                  </Typography>
                </Box>
              )}

              {userInfo.groupNumber && (
                <Box>
                  <Typography variant="subtitle2" color="textSecondary">
                    Group
                  </Typography>
                  <Typography variant="body1">
                    {userInfo.groupNumber}
                  </Typography>
                </Box>
              )}
            </Box>
          </CardContent>
        </Card>

        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              {userInfo.role === 'STUDENT' ? 'Student Features' : 'Admin Features'}
            </Typography>
            
            {userInfo.role === 'STUDENT' ? (
              <Typography variant="body2" color="textSecondary">
                Features for students like preference submission and viewing assignments will be implemented here.
              </Typography>
            ) : (
              <Box>
                <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                  Manage optional subjects and execute allocations.
                </Typography>
                
                <Button
                  variant="contained"
                  color="primary"
                  size="large"
                  startIcon={<AdminPanelSettings />}
                  onClick={() => window.location.href = '/optionals/assign'}
                  sx={{ mr: 2 }}
                >
                  Manage Optional Subject Assignments
                </Button>
              </Box>
            )}
          </CardContent>
        </Card>
      </Box>
    </Box>
  );
};

export default Dashboard;
