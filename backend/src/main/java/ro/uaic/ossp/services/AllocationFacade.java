package ro.uaic.ossp.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.Preference;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.models.enums.AllocationStrategy;
import ro.uaic.ossp.repositories.GradeRepository;
import ro.uaic.ossp.repositories.PreferenceRepository;
import ro.uaic.ossp.repositories.StudentRepository;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AllocationFacade {

    private final StudentRepository studentRepository;
    private final PreferenceRepository preferenceRepository;
    private final GradeRepository gradeRepository;
    private final AllocationService allocationService;

    public AllocationFacade(StudentRepository studentRepository,
                            PreferenceRepository preferenceRepository,
                            GradeRepository gradeRepository,
                            AllocationService allocationService) {
        this.studentRepository = studentRepository;
        this.preferenceRepository = preferenceRepository;
        this.gradeRepository = gradeRepository;
        this.allocationService = allocationService;
    }

    @Transactional(readOnly = true)
    public List<StudentAllocationDTO> executeAllocationByCriteria(int year, String specialization, String algorithm) {
        List<Student> students = studentRepository.findByAcademicYearAndSpecialization(year, specialization);
        if (students.isEmpty()) return Collections.emptyList();

        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        List<Preference> dbPrefs = preferenceRepository.findByStudentIdIn(studentIds);

        List<PreferenceDTO> prefs = dbPrefs.stream().map(p -> {
            PreferenceDTO dto = new PreferenceDTO();
            dto.setStudentId(p.getStudent().getId());
            dto.setCourseId(p.getOptionalCourse() != null ? p.getOptionalCourse().getId() : null);
            dto.setPriority(p.getPriority());
            return dto;
        }).collect(Collectors.toList());

        List<Grade> grades = gradeRepository.findByStudentIdInAndYearAndSpecialization(studentIds, year, specialization);
        Map<Long, List<Grade>> gradesByStudent = grades.stream().collect(Collectors.groupingBy(Grade::getStudentId));

        // Determine strategy (default GALE_SHAPLEY)
        AllocationStrategy strategy = AllocationStrategy.GALE_SHAPLEY;
        if (algorithm != null && !algorithm.isBlank()) {
            try {
                String normalized = algorithm.trim()
                        .replace('-', '_')
                        .replaceAll("\\s+", "_")
                        .replaceAll("(?<=[a-z0-9])(?=[A-Z])", "_")
                        .replaceAll("__+", "_")
                        .toUpperCase(Locale.ROOT);
                strategy = AllocationStrategy.valueOf(normalized);
            } catch (IllegalArgumentException ignored) {
                // keep default gale shapley
            }
        }

        // Run allocation
        List<StudentAllocationDTO> allocations = allocationService.executeAllocation(prefs, strategy);

        enrichWithNames(allocations, dbPrefs);

        return allocations;
    }

    // Populate studentName and allocatedCourseName on DTOs
    private void enrichWithNames(List<StudentAllocationDTO> allocations, List<Preference> dbPrefs) {
        if (allocations == null || allocations.isEmpty()) return;

        // Load students present in allocations
        List<Long> allocStudentIds = allocations.stream()
                .map(StudentAllocationDTO::getStudentId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Student> studentsById = new HashMap<>();
        if (!allocStudentIds.isEmpty()) {
            studentRepository.findAllById(allocStudentIds).forEach(s -> studentsById.put(s.getId(), s));
        }

        // Build map of courseId -> courseName from preferences (preferences already contain the optional course entity)
        Map<Long, String> courseNameById = new HashMap<>();
        for (Preference p : dbPrefs) {
            if (p.getOptionalCourse() == null) continue;
            Long cid = p.getOptionalCourse().getId();
            if (cid == null) continue;
            courseNameById.putIfAbsent(cid, resolveCourseName(p.getOptionalCourse()));
        }

        // Set names on DTOs
        for (StudentAllocationDTO dto : allocations) {
            if (dto.getStudentId() != null) {
                Student s = studentsById.get(dto.getStudentId());
                dto.setStudentName(resolveStudentName(s));
            }
            Long courseId = dto.getAllocatedCourseId();
            if (courseId != null) {
                dto.setAllocatedCourseName(courseNameById.get(courseId));
            }
        }
    }

    private String resolveStudentName(Student s) {
        if (s == null) return null;
        try {
            Method m = s.getClass().getMethod("getFullName");
            Object v = m.invoke(s);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        try {
            Method m = s.getClass().getMethod("getName");
            Object v = m.invoke(s);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        try {
            Method mf = s.getClass().getMethod("getFirstName");
            Method ml = s.getClass().getMethod("getLastName");
            Object f = mf.invoke(s);
            Object l = ml.invoke(s);
            if (f != null || l != null) {
                String first = f != null ? f.toString() : "";
                String last = l != null ? l.toString() : "";
                return (first + " " + last).trim();
            }
        } catch (Exception ignored) {}
        // fallback to toString
        return s.toString();
    }

    // Try common getters for course name on the optional course entity
    private String resolveCourseName(Object courseEntity) {
        if (courseEntity == null) return null;
        try {
            Method m = courseEntity.getClass().getMethod("getName");
            Object v = m.invoke(courseEntity);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        try {
            Method m = courseEntity.getClass().getMethod("getTitle");
            Object v = m.invoke(courseEntity);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        try {
            Method m = courseEntity.getClass().getMethod("getCourseName");
            Object v = m.invoke(courseEntity);
            if (v != null) return v.toString();
        } catch (Exception ignored) {}
        return courseEntity.toString();
    }
}