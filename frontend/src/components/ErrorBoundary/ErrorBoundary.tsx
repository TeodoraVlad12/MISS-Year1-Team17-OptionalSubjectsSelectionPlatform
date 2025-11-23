import { Component } from 'react';
import { Button, Typography, Paper, Container } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import type { ErrorBoundaryProps, ErrorBoundaryState } from './ErrorBoundary.types';
import './ErrorBoundary.styles.scss';

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
    constructor(props: ErrorBoundaryProps) {
        super(props);
        this.state = {
            hasError: false,
            error: null,
        };
    }

    static getDerivedStateFromError(error: Error): ErrorBoundaryState {
        return {
            hasError: true,
            error,
        };
    }

    componentDidCatch(error: Error, errorInfo: React.ErrorInfo): void {
        console.error('ErrorBoundary caught an error:', error, errorInfo);
    }

    render() {
        if (this.state.hasError) {
            if (this.props.fallback) {
                return this.props.fallback;
            }

            return (
                <div className="error-boundary">
                    <Container maxWidth="sm" className="error-boundary__container">
                        <Paper elevation={3} className="error-boundary__paper">
                            <ErrorOutlineIcon color="error" className="error-boundary__icon" />
                            <Typography variant="h4" gutterBottom className="error-boundary__title">
                                Something went wrong
                            </Typography>
                            <Typography variant="body1" color="text.secondary" paragraph className="error-boundary__message">
                                We're sorry, but something unexpected happened.
                            </Typography>
                            {this.state.error && (
                                <div className="error-boundary__details-wrapper">
                                    <details>
                                        <summary className="error-boundary__details-summary">
                                            Error details
                                        </summary>
                                        <pre className="error-boundary__details-content">
                                            {this.state.error.toString()}
                                        </pre>
                                    </details>
                                </div>
                            )}
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => window.location.reload()}
                                className="error-boundary__button"
                            >
                                Reload page
                            </Button>
                        </Paper>
                    </Container>
                </div>
            );
        }

        return this.props.children;
    }
}
