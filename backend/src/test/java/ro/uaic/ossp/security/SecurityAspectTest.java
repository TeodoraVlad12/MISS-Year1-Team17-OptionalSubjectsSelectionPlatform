package ro.uaic.ossp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ro.uaic.ossp.controllers.AllocationController;
import ro.uaic.ossp.models.enums.UserRole;
import ro.uaic.ossp.security.exceptions.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "logging.level.ro.uaic.ossp.security=INFO"
})
class SecurityAspectTest {

    @Autowired
    private AllocationController allocationController;

    @Autowired
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityContext.clearContext();
    }

    @Test
    void testAllocationAccess_WithAdminRole_ShouldSucceed() {
        securityContext.setCurrentUser("admin1", UserRole.ADMIN);
        
        assertDoesNotThrow(() -> {
            try {
                allocationController.runAllocation(null);
            } catch (Exception e) {
                if (!(e instanceof AccessDeniedException)) {
                    return;
                }
                throw e;
            }
        });
    }

    @Test
    void testAllocationAccess_WithSecretaryRole_ShouldSucceed() {
        securityContext.setCurrentUser("secretary1", UserRole.SECRETARY);
        
        assertDoesNotThrow(() -> {
            try {
                allocationController.runAllocation(null);
            } catch (Exception e) {
                if (!(e instanceof AccessDeniedException)) {
                    return;
                }
                throw e;
            }
        });
    }

    @Test
    void testAllocationAccess_WithStudentRole_ShouldFail() {
        securityContext.setCurrentUser("student1", UserRole.STUDENT);
        
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            allocationController.runAllocation(null);
        });
        
        assertEquals("Access denied: insufficient privileges", exception.getMessage());
    }

    @Test
    void testSecurityContext_UserManagement() {
        securityContext.setCurrentUser("test-user", UserRole.ADMIN);
        
        assertTrue(securityContext.hasRole(UserRole.ADMIN));
        assertFalse(securityContext.hasRole(UserRole.STUDENT));
        assertTrue(securityContext.hasAnyRole(UserRole.ADMIN, UserRole.SECRETARY));
        
        SecurityContext.UserContext user = securityContext.getCurrentUser();
        assertEquals("test-user", user.getUserId());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testSecurityContext_DefaultUser() {
        securityContext.clearContext();
        
        SecurityContext.UserContext user = securityContext.getCurrentUser();
        assertEquals("demo-admin", user.getUserId());
        assertEquals(UserRole.ADMIN, user.getRole());
    }
}

//to run the tests from the backend dir: ./mvnw test -Dtest=SecurityAspectTest