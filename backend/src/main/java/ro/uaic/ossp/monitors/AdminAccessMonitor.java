package ro.uaic.ossp.monitors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.security.exceptions.AccessDeniedException;

@Aspect
@Component
public class AdminAccessMonitor {

    // Intercepts calls to GradeService.uploadCsv
    @Before("execution(* ro.uaic.ossp.services.GradeService.uploadCsv(..))")
    public void checkAdminAccess() {
        // Check user role (example with Spring Security)
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN"))) {
            throw new AccessDeniedException("Only ADMIN can upload CSV");
        }
    }
}
