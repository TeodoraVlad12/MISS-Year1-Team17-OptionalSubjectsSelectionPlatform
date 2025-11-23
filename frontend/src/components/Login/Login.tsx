import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Container
} from '@mui/material';
import { LoginTwoTone as LoginIcon } from '@mui/icons-material';
import { useAuth } from '../../contexts/AuthContext';
import type { LoginRequest } from '../../services/AuthService';

const Login: React.FC = () => {
  const { login } = useAuth();
  const [formData, setFormData] = useState<LoginRequest>({
    email: '',
    password: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await login(formData);
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const isValidEmail = (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const isFormValid = formData.email && 
                     formData.password && 
                     isValidEmail(formData.email);

  return (
    <Container maxWidth="sm">
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight="100vh"
      >
        <Card sx={{ width: '100%', maxWidth: 400 }}>
          <CardContent sx={{ p: 4 }}>
            <Box display="flex" flexDirection="column" alignItems="center" mb={3}>
              <LoginIcon sx={{ fontSize: 48, color: 'primary.main', mb: 2 }} />
              <Typography variant="h4" component="h1" gutterBottom>
                ElectiveMatch
              </Typography>
              <Typography variant="h6" color="textSecondary">
                Optional Subjects Selection
              </Typography>
            </Box>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <TextField
                fullWidth
                label="Institutional Email"
                name="email"
                type="email"
                value={formData.email}
                onChange={handleInputChange}
                margin="normal"
                required
                disabled={loading}
                placeholder="john.doe.123@student.uaic.ro"
                helperText="Use @student.uaic.ro for students or @uaic.ro for admins"
              />

              <TextField
                fullWidth
                label="Password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleInputChange}
                margin="normal"
                required
                disabled={loading}
              />

              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
                disabled={loading || !isFormValid}
                size="large"
              >
                {loading ? (
                  <CircularProgress size={24} color="inherit" />
                ) : (
                  'Sign In'
                )}
              </Button>
            </form>

            {/*<Box mt={2}>*/}
            {/*  <Typography variant="body2" color="textSecondary" align="center">*/}
            {/*    Demo Accounts:*/}
            {/*  </Typography>*/}
            {/*  <Typography variant="caption" display="block" align="center">*/}
            {/*    Student: john.doe.123@student.uaic.ro*/}
            {/*  </Typography>*/}
            {/*  <Typography variant="caption" display="block" align="center">*/}
            {/*    Admin: admin.secretary@uaic.ro*/}
            {/*  </Typography>*/}
            {/*  <Typography variant="caption" display="block" align="center">*/}
            {/*    Password: any value*/}
            {/*  </Typography>*/}
            {/*</Box>*/}
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default Login;
