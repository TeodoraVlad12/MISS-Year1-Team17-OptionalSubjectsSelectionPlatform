import { createTheme } from "@mui/material/styles";

// Theme inspired by UAIC student catalog
export const theme = createTheme({
    palette: {
        primary: {
            main: "#0078B5", // Blue from the header
            dark: "#005A8C",
            light: "#339AC8",
            contrastText: "#ffffff",
        },
        secondary: {
            main: "#B5274D", // Accent red/pink
            dark: "#8C1F3C",
            light: "#C44A6A",
            contrastText: "#ffffff",
        },
        background: {
            default: "#f5f5f5",
            paper: "#ffffff",
        },
        text: {
            primary: "#003D5C", // Dark blue for headings
            secondary: "#666666",
        },
        error: {
            main: "#d32f2f",
        },
        success: {
            main: "#2e7d32",
        },
        warning: {
            main: "#ed6c02",
        },
        info: {
            main: "#0288d1",
        },
    },
    typography: {
        fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
        h1: {
            fontSize: "2.5rem",
            fontWeight: 600,
            color: "#003D5C",
        },
        h2: {
            fontSize: "2rem",
            fontWeight: 600,
            color: "#003D5C",
        },
        h3: {
            fontSize: "1.75rem",
            fontWeight: 600,
            color: "#003D5C",
        },
        h4: {
            fontSize: "1.5rem",
            fontWeight: 600,
            color: "#003D5C",
        },
        h5: {
            fontSize: "1.25rem",
            fontWeight: 600,
            color: "#003D5C",
        },
        h6: {
            fontSize: "1rem",
            fontWeight: 600,
            color: "#003D5C",
        },
        button: {
            textTransform: "none",
            fontWeight: 600,
        },
    },
    shape: {
        borderRadius: 8,
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    borderRadius: 24,
                    padding: "10px 24px",
                    fontSize: "1rem",
                },
                contained: {
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                    "&:hover": {
                        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.15)",
                    },
                },
            },
        },
        MuiAppBar: {
            styleOverrides: {
                root: {
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
                    borderRadius: 8,
                },
            },
        },
    },
});
