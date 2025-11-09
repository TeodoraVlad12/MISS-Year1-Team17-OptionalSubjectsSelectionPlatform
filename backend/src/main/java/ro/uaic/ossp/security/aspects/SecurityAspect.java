package ro.uaic.ossp.security.aspects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.enums.UserRole;
import ro.uaic.ossp.security.SecurityContext;
import ro.uaic.ossp.security.annotations.RequireRole;
import ro.uaic.ossp.security.exceptions.AccessDeniedException;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityAspect {
    
    private final SecurityContext securityContext;
    
    @Around("@annotation(ro.uaic.ossp.security.annotations.RequireRole)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        Method method = ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        
        SecurityContext.UserContext currentUser = securityContext.getCurrentUser();
        
        log.info("Security check for {}.{} - User: {} ({})", 
                className, methodName, currentUser.getUserId(), currentUser.getRole());
        
        UserRole[] requiredRoles = requireRole.value();
        boolean hasAccess = securityContext.hasAnyRole(requiredRoles);
        
        if (!hasAccess) {
            log.warn("Access denied for user {} attempting to access {}.{} - Required roles: {}", 
                    currentUser.getUserId(), className, methodName, requiredRoles);
            throw new AccessDeniedException(requireRole.message());
        }
        
        log.info("Access granted for user {} to {}.{}", 
                currentUser.getUserId(), className, methodName);
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Method {}.{} executed successfully in {}ms", 
                    className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Method {}.{} failed after {}ms - Error: {}", 
                    className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}
