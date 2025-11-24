import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Router } from '../Router/Router';
import { ErrorBoundary } from '../ErrorBoundary/ErrorBoundary';
import { theme } from '../../library/theme';
import { ServicesProvider } from '../../services/ServicesContext';
import { AuthProvider } from '../../contexts/AuthContext';

function App() {
    return (
        <ErrorBoundary>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <ServicesProvider>
                    <AuthProvider>
                        <Router />
                    </AuthProvider>
                </ServicesProvider>
            </ThemeProvider>
        </ErrorBoundary>
    );
}

export default App;
