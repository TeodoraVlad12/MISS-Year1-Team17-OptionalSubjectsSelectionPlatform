import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Router } from '../Router/Router';
import { ErrorBoundary } from '../ErrorBoundary/ErrorBoundary';
import { theme } from '../../library/theme';
import { ServicesProvider } from '../../services/ServicesContext';

function App() {
    return (
        <ErrorBoundary>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <ServicesProvider>
                    <Router />
                </ServicesProvider>
            </ThemeProvider>
        </ErrorBoundary>
    );
}

export default App;
