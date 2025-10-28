package com.example.ossp.adapters;

/**
 * Adapter interface - defines what our system expects.
 */
public interface AuthenticationAdapter {
    /**
     * Authenticate student and return data in our system's format.
     */
    StudentAuthData authenticate(String email, String password);
}
