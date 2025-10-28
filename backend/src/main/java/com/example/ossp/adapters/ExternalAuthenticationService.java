package com.example.ossp.adapters;

/**
 * External university authentication API (third-party service).
 * This represents the incompatible interface we need to adapt.
 */
public class ExternalAuthenticationService {
    
    /**
     * External API method with different signature and return format.
     * Returns data in format: "MATRICOL:12345|NAME:John Doe|EMAIL:john@student.uaic.ro"
     */
    public String authenticateStudent(String email, String password) {
        // Simulates external API call
        // In reality, this would make HTTP request to university API
        if (email.endsWith("@student.uaic.ro")) {
            return "MATRICOL:2024001|NAME:John Doe|EMAIL:" + email;
        }
        throw new RuntimeException("Authentication failed");
    }
}
