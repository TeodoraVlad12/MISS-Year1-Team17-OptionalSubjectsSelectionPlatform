import { Container, Typography, Paper } from '@mui/material';
import { useRouteError, isRouteErrorResponse } from 'react-router-dom';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import './RouteError.styles.scss';

export const RouteError = () => {
    const error = useRouteError();

    let errorMessage = 'An unexpected error occurred';
    let errorDetails = '';

    if (isRouteErrorResponse(error)) {
        errorMessage = `${error.status} ${error.statusText}`;
        errorDetails = error.data?.message || '';
    } else if (error instanceof Error) {
        errorMessage = error.message;
        errorDetails = error.stack || '';
    }

    return (
        <div className="route-error">
            <Container maxWidth="sm" className="route-error__container">
                <Paper elevation={3} className="route-error__paper">
                    <ErrorOutlineIcon color="error" className="route-error__icon" />
                    <Typography variant="h4" gutterBottom className="route-error__title">
                        {errorMessage}
                    </Typography>
                    <Typography variant="body1" color="text.secondary" paragraph className="route-error__message">
                        The page you're looking for doesn't exist or something went wrong.
                    </Typography>
                    {errorDetails && (
                        <div className="route-error__details-wrapper">
                            <details>
                                <summary className="route-error__details-summary">
                                    Error details
                                </summary>
                                <pre className="route-error__details-content">
                                    {errorDetails}
                                </pre>
                            </details>
                        </div>
                    )}
                </Paper>
            </Container>
        </div>
    );
};
