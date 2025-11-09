package ro.uaic.ossp.security.annotations;

import ro.uaic.ossp.models.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for method-level role-based access control.
 * Uses AOP to intercept method calls and validate user permissions.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * Required roles to access the annotated method.
     * User must have at least one of the specified roles.
     */
    UserRole[] value();
    
    /**
     * Custom error message when access is denied.
     */
    String message() default "Access denied: insufficient privileges";
}
