package ro.uaic.ossp.services.strategies.implementations;

import org.springframework.stereotype.Component;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.interfaces.IAllocationStrategy;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GradeBasedStrategy implements IAllocationStrategy {

    @Override
    public List<StudentAllocationDTO> executeAllocation(List<PreferenceDTO> preferences) {
        // Return empty list if no preferences are provided
        if (preferences == null || preferences.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort preferences so that:
        // 1. Students with higher "grades" (represented by higher studentId) are processed first
        // 2. Within the same student, lower priority numbers (1, 2, 3) are processed first
        List<PreferenceDTO> sortedPrefs = preferences.stream()
                .sorted(Comparator
                        .comparing(PreferenceDTO::getStudentId).reversed()
                        .thenComparing(PreferenceDTO::getPriority))
                .collect(Collectors.toList());

        // Keep track of already allocated students and courses
        Set<Long> allocatedStudents = new HashSet<>();
        Set<Long> allocatedCourses = new HashSet<>();
        List<StudentAllocationDTO> allocations = new ArrayList<>();

        // Iterate over sorted preferences to allocate courses
        for (PreferenceDTO pref : sortedPrefs) {
            Long studentId = pref.getStudentId();
            Long courseId = pref.getCourseId();

            // Allocate only if the student is not already assigned
            // and the course is still available
            if (!allocatedStudents.contains(studentId) && !allocatedCourses.contains(courseId)) {
                StudentAllocationDTO allocation = StudentAllocationDTO.builder()
                        .studentId(studentId)
                        .studentName(null) // Not available at this stage
                        .allocatedCourseId(courseId)
                        .allocatedCourseName(null) // Not available at this stage
                        .preferenceRank(pref.getPriority())
                        .build();

                allocations.add(allocation);
                allocatedStudents.add(studentId);
                allocatedCourses.add(courseId);
            }
        }

        // Return final allocation results
        return allocations;
    }
}
