import React from 'react';
import {
  Dialog,
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Slide,
  Box,
  Button,
  List,
  ListItem,
  ListItemText,
  Container
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import InfoIcon from '@mui/icons-material/Info';

const Transition = React.forwardRef(function Transition(props, ref) {
  return <Slide direction="up" ref={ref} {...props} />;
});

export default function FullscreenMonitor({
  open,
  messages = [],
  severity = 'info',
  title = 'Registration notice',
  onClose,
  onContinue
}) {
  const icon = {
    success: <CheckCircleIcon fontSize="large" color="success" />,
    info: <InfoIcon fontSize="large" color="info" />,
    warning: <WarningAmberIcon fontSize="large" color="warning" />,
    error: <WarningAmberIcon fontSize="large" color="error" />
  }[severity];

  return (
    <Dialog fullScreen open={open} onClose={onClose} TransitionComponent={Transition}>
      <AppBar sx={{ position: 'relative' }}>
        <Toolbar>
          <Typography sx={{ flex: 1 }} variant="h6" component="div">{title}</Typography>
          <IconButton edge="end" color="inherit" onClick={onClose} aria-label="close">
            <CloseIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Container sx={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <Box textAlign="center" px={2}>
          <Box mb={2}>{icon}</Box>
          <Typography variant="h4" component="h2" gutterBottom>
            {severity === 'success' ? 'Registration completed' : severity === 'error' ? 'Attention required' : 'Important information'}
          </Typography>

          <Box my={3} sx={{ maxWidth: 800, margin: '0 auto' }}>
            <List>
              {messages && messages.length > 0 ? (
                messages.map((m, i) => (
                  <ListItem key={i}>
                    <ListItemText primary={m} />
                  </ListItem>
                ))
              ) : (
                <ListItem>
                  <ListItemText primary="No messages" />
                </ListItem>
              )}
            </List>
          </Box>

          <Box display="flex" gap={2} justifyContent="center" mt={4}>
            {onContinue && (
              <Button variant="contained" color="primary" size="large" onClick={onContinue}>
                Continue
              </Button>
            )}
            <Button variant="outlined" color="inherit" size="large" onClick={onClose}>
              Close
            </Button>
          </Box>
        </Box>
      </Container>
    </Dialog>
  );
}