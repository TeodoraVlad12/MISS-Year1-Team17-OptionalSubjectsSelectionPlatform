package ro.uaic.ossp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.enums.UserRole;

@Component
@Slf4j
public class SecurityContext {
    
    private static final ThreadLocal<UserContext> currentUser = new ThreadLocal<>();
    
    public void setCurrentUser(String userId, UserRole role) {
        UserContext context = new UserContext(userId, role);
        currentUser.set(context);
        log.debug("Set current user context: userId={}, role={}", userId, role);
    }
    
    public UserContext getCurrentUser() {
        UserContext context = currentUser.get();
        if (context == null) {
            log.warn("No user context found, using default ADMIN user for demo");
            return new UserContext("demo-admin", UserRole.ADMIN);
        }
        return context;
    }
    
    public boolean hasRole(UserRole role) {
        UserContext context = getCurrentUser();
        return context.getRole() == role;
    }
    
    public boolean hasAnyRole(UserRole... roles) {
        UserContext context = getCurrentUser();
        for (UserRole role : roles) {
            if (context.getRole() == role) {
                return true;
            }
        }
        return false;
    }
    
    public void clearContext() {
        currentUser.remove();
        log.debug("Cleared user context");
    }
}
