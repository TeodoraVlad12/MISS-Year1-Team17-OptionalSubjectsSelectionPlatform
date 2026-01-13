import React, { useState } from 'react';
import { AuthService } from '../../services/AuthService';
import type { RegisterRequest, RegisterResponse } from '../../services/AuthService';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Container,
  Snackbar,
  Stack,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  List,
  ListItem,
  ListItemText
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNavigate } from 'react-router-dom';
import FullscreenMonitor from '../FullscreenMonitor/FullscreenMonitor.jsx';

const apiBase = (globalThis as any).process?.env?.REACT_APP_API_BASE || 'http://localhost:8080';
const authService = new AuthService(apiBase);

const severityFromMessages = (msgs: string[] = []): 'success' | 'info' | 'warning' | 'error' => {
  const text = msgs.join(' ').toLowerCase();
  if (!text) return 'info';
  if (text.includes('failed') || text.includes('error') || text.includes('invalid') || text.includes('violate')) return 'error';
  if (text.includes('warning')) return 'warning';
  if (text.includes('ok') || text.includes('successful') || text.includes('registered')) return 'success';
  return 'info';
};

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<RegisterRequest>({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    matriculationNumber: '',
    academicYear: 1,
    specialization: '',
    groupNumber: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [monitorMessages, setMonitorMessages] = useState<string[]>([]);
  const [snackOpen, setSnackOpen] = useState(false);
  const [snackSeverity, setSnackSeverity] = useState<'success' | 'info' | 'warning' | 'error'>('info');
  const [dialogOpen, setDialogOpen] = useState(false);

  // fullscreen monitor control
  const [fullscreenOpen, setFullscreenOpen] = useState(false);
  const [fullscreenSeverity, setFullscreenSeverity] = useState<'success' | 'info' | 'warning' | 'error'>('info');

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: name === 'academicYear' ? Number(value) : value }));
  };

  const isValidEmail = (email: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const isFormValid =
    !!formData.email &&
    !!formData.password &&
    !!formData.firstName &&
    !!formData.lastName &&
    isValidEmail(formData.email);

  const openMonitorSnack = (msgs: string[]) => {
    setMonitorMessages(msgs || []);
    setSnackSeverity(severityFromMessages(msgs));
    setSnackOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setMonitorMessages([]);
    try {
      const resp: RegisterResponse = await authService.register(formData);

      if (resp?.monitorMessages && resp.monitorMessages.length > 0) {
        setMonitorMessages(resp.monitorMessages);
        const sev = severityFromMessages(resp.monitorMessages);
        setFullscreenSeverity(sev);
        setFullscreenOpen(true);         // show fullscreen monitor
        openMonitorSnack(resp.monitorMessages);
      }

      if (resp && resp.success) {
        if (!resp.monitorMessages || resp.monitorMessages.length === 0) {
          openMonitorSnack(['Registration successful']);
          setFullscreenSeverity('success');
          setMonitorMessages(['Registration successful']);
          setFullscreenOpen(true);
        }
        // navigate after user confirms in fullscreen dialog or after small delay
      } else {
        const msg = resp?.message || 'Registration failed';
        setError(msg);
        if (resp?.monitorMessages) {
          setMonitorMessages(resp.monitorMessages);
          setFullscreenSeverity(severityFromMessages(resp.monitorMessages));
          setFullscreenOpen(true);
        }
      }
    } catch (err: any) {
      const msg = err instanceof Error ? err.message : String(err);
      setError(msg);
      setMonitorMessages(err?.monitorMessages && Array.isArray(err.monitorMessages) ? err.monitorMessages : [msg]);
      setFullscreenSeverity('error');
      setFullscreenOpen(true);
      openMonitorSnack([msg]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" minHeight="100vh">
        <Card sx={{ width: '100%', maxWidth: 560 }}>
          <CardContent sx={{ p: 4 }}>
            <Box display="flex" flexDirection="column" alignItems="center" mb={2}>
              <Typography variant="h4" component="h1" gutterBottom>Register</Typography>
              <Typography variant="body2" color="text.secondary">Create an account for ElectiveMatch</Typography>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <form onSubmit={handleSubmit}>
              <Stack spacing={2}>
                <TextField fullWidth label="First name" name="firstName" value={formData.firstName} onChange={handleInputChange} required disabled={loading} />
                <TextField fullWidth label="Last name" name="lastName" value={formData.lastName} onChange={handleInputChange} required disabled={loading} />
                <TextField fullWidth label="Institutional Email" name="email" type="email" value={formData.email} onChange={handleInputChange} required disabled={loading} placeholder="john.doe.123@student.uaic.ro" helperText="Use @student.uaic.ro for students or @uaic.ro for admins" />
                <TextField fullWidth label="Password" name="password" type="password" value={formData.password} onChange={handleInputChange} required disabled={loading} />
                <TextField fullWidth label="Matriculation number" name="matriculationNumber" value={formData.matriculationNumber} onChange={handleInputChange} disabled={loading} />
                <TextField fullWidth label="Academic year" name="academicYear" type="number" value={String(formData.academicYear)} onChange={handleInputChange} disabled={loading} />
                <TextField fullWidth label="Specialization" name="specialization" value={formData.specialization} onChange={handleInputChange} disabled={loading} />
                <TextField fullWidth label="Group number" name="groupNumber" value={formData.groupNumber} onChange={handleInputChange} disabled={loading} />

                <Button type="submit" fullWidth variant="contained" disabled={loading || !isFormValid} size="large">
                  {loading ? <CircularProgress size={24} color="inherit" /> : 'Register'}
                </Button>
              </Stack>
            </form>
          </CardContent>
        </Card>
      </Box>

      <Snackbar
        open={snackOpen}
        onClose={() => setSnackOpen(false)}
        autoHideDuration={snackSeverity === 'error' ? null : 6000}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert
          severity={snackSeverity}
          action={
            <>
              <Button color="inherit" size="small" onClick={() => setDialogOpen(true)}>Details</Button>
              <IconButton size="small" color="inherit" onClick={() => setSnackOpen(false)}>
                <CloseIcon fontSize="small" />
              </IconButton>
            </>
          }
          sx={{ width: '100%' }}
        >
          {monitorMessages && monitorMessages.length > 0 ? monitorMessages[0] : (snackSeverity === 'success' ? 'Registration successful' : 'Notification')}
        </Alert>
      </Snackbar>

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Registration Monitor Messages</DialogTitle>
        <DialogContent dividers>
          {monitorMessages && monitorMessages.length > 0 ? (
            <List>
              {monitorMessages.map((m, i) => (
                <ListItem key={i} divider>
                  <ListItemText primary={m} />
                </ListItem>
              ))}
            </List>
          ) : (
            <Typography>No monitor messages</Typography>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => { navigator.clipboard?.writeText(monitorMessages.join('\n')); }} size="small">Copy</Button>
          <Button onClick={() => setDialogOpen(false)} size="small">Close</Button>
        </DialogActions>
      </Dialog>

      {/* Fullscreen monitor - MUI Dialog uses a portal so it covers the viewport */}
      <FullscreenMonitor
        open={fullscreenOpen}
        messages={monitorMessages}
        severity={fullscreenSeverity}
        title="Registration Notice"
        onClose={() => setFullscreenOpen(false)}
        onContinue={() => {
          setFullscreenOpen(false);
          navigate('/login');
        }}
      />
    </Container>
  );
};

export default Register;