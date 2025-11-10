package ro.uaic.ossp.services.strategies.implementations;

import org.springframework.stereotype.Component;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.interfaces.IAllocationStrategy;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class GaleShapleyStrategy implements IAllocationStrategy {

    @Override
    public List<StudentAllocationDTO> executeAllocation(List<PreferenceDTO> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return Collections.emptyList();
        }

        // Build a map: studentId -> ordered list of courseIds (based on priority)
        Map<Long, List<PreferenceDTO>> studentPrefs = preferences.stream()
                .collect(Collectors.groupingBy(
                        PreferenceDTO::getStudentId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(PreferenceDTO::getPriority))
                                        .collect(Collectors.toList())
                        )
                ));

        // Set of all available courses
        Set<Long> freeCourses = preferences.stream()
                .map(PreferenceDTO::getCourseId)
                .collect(Collectors.toSet());

        // Initially, all students are free and unmatched
        Queue<Long> freeStudents = new LinkedList<>(studentPrefs.keySet());

        // Current matches: courseId -> studentId
        Map<Long, Long> courseToStudent = new HashMap<>();
        // Track which course index each student will propose next
        Map<Long, Integer> studentProposalIndex = new HashMap<>();

        for (Long studentId : studentPrefs.keySet()) {
            studentProposalIndex.put(studentId, 0);
        }

        // Gale-Shapley main loop
        while (!freeStudents.isEmpty()) {
            Long student = freeStudents.poll();
            List<PreferenceDTO> prefs = studentPrefs.get(student);

            // If student has proposed to all courses, skip
            int currentIndex = studentProposalIndex.get(student);
            if (currentIndex >= prefs.size()) continue;

            PreferenceDTO nextPref = prefs.get(currentIndex);
            Long course = nextPref.getCourseId();
            studentProposalIndex.put(student, currentIndex + 1);

            // If course is free, match it
            if (!courseToStudent.containsKey(course)) {
                courseToStudent.put(course, student);
            } else {
                // Course already taken — apply tie-breaking rule (lower studentId wins for stability)
                Long currentStudent = courseToStudent.get(course);
                if (student < currentStudent) {
                    // New student replaces current one
                    courseToStudent.put(course, student);
                    freeStudents.add(currentStudent);
                } else {
                    // Student remains free, will try next preference
                    freeStudents.add(student);
                }
            }
        }

        // Build final allocation list
        List<StudentAllocationDTO> allocations = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : courseToStudent.entrySet()) {
            Long courseId = entry.getKey();
            Long studentId = entry.getValue();

            int prefRank = studentPrefs.get(studentId).stream()
                    .filter(p -> p.getCourseId().equals(courseId))
                    .map(PreferenceDTO::getPriority)
                    .findFirst()
                    .orElse(1);

            allocations.add(StudentAllocationDTO.builder()
                    .studentId(studentId)
                    .studentName(null)
                    .allocatedCourseId(courseId)
                    .allocatedCourseName(null)
                    .preferenceRank(prefRank)
                    .build());
        }

        return allocations;
    }
}
