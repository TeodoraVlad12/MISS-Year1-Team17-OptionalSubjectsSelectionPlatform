import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Button,
  Chip
} from '@mui/material';
import { Logout, Home, School, AdminPanelSettings } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';

interface TopBarProps {
  title?: string;
  showRoleChip?: boolean;
  showHomeButton?: boolean;
}

const TopBar: React.FC<TopBarProps> = ({
  title = "ElectiveMatch - Optional Subjects Selection",
  showRoleChip = false,
  showHomeButton = false
}) => {
  const { user: userInfo, logout } = useAuth();

  if (!userInfo) {
    return null;
  }

  const handleLogout = () => {
    logout();
  };

  const handleGoHome = () => {
    window.location.href = '/dashboard';
  };

  const getRoleIcon = () => {
    return userInfo.role === 'STUDENT' ? <School /> : <AdminPanelSettings />;
  };

  const getRoleColor = () => {
    return userInfo.role === 'STUDENT' ? 'primary' : 'secondary';
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          {title}
        </Typography>
        
        <Box display="flex" alignItems="center" gap={2}>
          {showRoleChip && (
            <Chip 
              icon={getRoleIcon()} 
              label={userInfo.role}
              color={getRoleColor() as any}
              variant="outlined"
              sx={{ color: 'white', borderColor: 'white' }}
            />
          )}
          
          <Typography variant="body2">
            {userInfo.firstName} {userInfo.lastName}
          </Typography>
          
          {showHomeButton && (
            <Button 
              color="inherit" 
              onClick={handleGoHome}
              startIcon={<Home />}
            >
              Home
            </Button>
          )}
          
          <Button 
            color="inherit" 
            onClick={handleLogout}
            startIcon={<Logout />}
          >
            Logout
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;
