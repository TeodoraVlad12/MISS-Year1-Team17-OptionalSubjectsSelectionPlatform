package ro.uaic.ossp.monitors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.security.exceptions.BadRequestException;

@Aspect
@Component
public class GradeValueMonitor {

    // Runs before saving GradeEntry
    @Before("execution(* ro.uaic.ossp.models.GradeEntry.setGradeValue(..)) && args(value)")
    public void validateGrade(double value) {

        // Auto-fix formatting
        if (Double.isNaN(value))
            throw new BadRequestException("Grade is NaN");

        if (value < 4) value = 4;
        if (value > 10) value = 10;
    }
}

