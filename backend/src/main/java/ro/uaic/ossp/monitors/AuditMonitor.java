package ro.uaic.ossp.monitors;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.GradeEntry;
import ro.uaic.ossp.repositories.GradeRepository;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditMonitor {

    private final GradeRepository repo;

    /**
     * Runs BEFORE saving a Grade.
     * Compares old grade entries with new ones and logs changed values.
     */
    @Before("execution(* ro.uaic.ossp.repositories.GradeRepository.save(..)) && args(grade)")
    public void auditChanges(Grade grade) {

        repo.findByMatricol(grade.getMatricol()).ifPresent(existing -> {

            for (GradeEntry oldEntry : existing.getEntries()) {

                // Find matching new entry by mandatory/optional course ID
                grade.getEntries().stream()
                        .filter(newEntry -> isSameCourse(oldEntry, newEntry))
                        .findFirst()
                        .ifPresent(newEntry -> {

                            Double oldVal = oldEntry.getGradeValue();
                            Double newVal = newEntry.getGradeValue();

                            if (oldVal == null) oldVal = 0.0;
                            if (newVal == null) newVal = 0.0;

                            if (!oldVal.equals(newVal)) {
                                System.out.println(
                                        "[AuditMonitor] Grade changed for course '" +
                                                getCourseName(oldEntry) +
                                                "' from " + oldVal +
                                                " to " + newVal
                                );
                            }
                        });
            }
        });
    }

    /**
     * Checks if two GradeEntry objects refer to the same course.
     */
    private boolean isSameCourse(GradeEntry a, GradeEntry b) {

        if (a.getMandatoryCourse() != null && b.getMandatoryCourse() != null) {
            return a.getMandatoryCourse().getId().equals(b.getMandatoryCourse().getId());
        }

        if (a.getOptionalCourse() != null && b.getOptionalCourse() != null) {
            return a.getOptionalCourse().getId().equals(b.getOptionalCourse().getId());
        }

        return false;
    }

    /**
     * Returns the course name regardless of type.
     */
    private String getCourseName(GradeEntry entry) {
        if (entry.getMandatoryCourse() != null)
            return entry.getMandatoryCourse().getName();
        if (entry.getOptionalCourse() != null)
            return entry.getOptionalCourse().getName();
        return "UNKNOWN COURSE";
    }
}
