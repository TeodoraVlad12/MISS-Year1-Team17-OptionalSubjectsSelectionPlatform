package ro.uaic.ossp.security;

import ro.uaic.ossp.models.enums.UserRole;

public class UserContext {
    private final String userId;
    private final UserRole role;
    
    public UserContext(String userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        return String.format("UserContext{userId='%s', role=%s}", userId, role);
    }
}
