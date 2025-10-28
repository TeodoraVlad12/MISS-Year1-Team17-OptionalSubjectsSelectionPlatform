package com.example.ossp.adapters;

/**
 * Adapter implementation - converts external API format to our system's format.
 * This is the Adapter pattern in action!
 */
public class UniversityAuthAdapter implements AuthenticationAdapter {
    
    private final ExternalAuthenticationService externalService;
    
    public UniversityAuthAdapter() {
        this.externalService = new ExternalAuthenticationService();
    }
    
    @Override
    public StudentAuthData authenticate(String email, String password) {
        String externalResponse = externalService.authenticateStudent(email, password);
        
        return parseExternalResponse(externalResponse);
    }
    
    /**
     * Converts external format to our StudentAuthData format.
     * External: "MATRICOL:12345|NAME:John Doe|EMAIL:john@student.uaic.ro"
     * Our format: StudentAuthData object
     */
    private StudentAuthData parseExternalResponse(String response) {
        String[] parts = response.split("\\|");
        String matricol = parts[0].split(":")[1];
        String name = parts[1].split(":")[1];
        String email = parts[2].split(":")[1];
        
        return new StudentAuthData(matricol, name, email);
    }
}
