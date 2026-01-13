package ro.uaic.ossp.monitors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.GradeEntry;
import ro.uaic.ossp.models.MandatoryCourse;
import ro.uaic.ossp.models.OptionalCourse;

@Aspect
@Component
public class DuplicateCourseMonitor {

    /**
     * Ensures that a grade contains only one entry per course.
     * If a duplicate is added, the monitor keeps the higher grade
     * and removes the older one.
     */
    @Before("execution(* ro.uaic.ossp.models.Grade.addEntry(..)) && args(entry)")
    public void preventDuplicates(JoinPoint jp, GradeEntry entry) {

        Grade grade = (Grade) jp.getTarget();

        Long newMandatoryId = entry.getMandatoryCourse() != null ? entry.getMandatoryCourse().getId() : null;
        Long newOptionalId  = entry.getOptionalCourse()  != null ? entry.getOptionalCourse().getId()  : null;

        // Remove duplicate entries
        grade.getEntries().removeIf(existing -> {

            Long existingMandatoryId = existing.getMandatoryCourse() != null ? existing.getMandatoryCourse().getId() : null;
            Long existingOptionalId  = existing.getOptionalCourse()  != null ? existing.getOptionalCourse().getId()  : null;

            boolean sameMandatory = newMandatoryId != null && newMandatoryId.equals(existingMandatoryId);
            boolean sameOptional  = newOptionalId  != null && newOptionalId.equals(existingOptionalId);

            if (sameMandatory || sameOptional) {

                // Keep the higher grade
                double bestValue = Math.max(
                        existing.getGradeValue() != null ? existing.getGradeValue() : 0,
                        entry.getGradeValue() != null ? entry.getGradeValue() : 0
                );

                entry.setGradeValue(bestValue);
                return true; // remove old entry
            }

            return false;
        });
    }
}
