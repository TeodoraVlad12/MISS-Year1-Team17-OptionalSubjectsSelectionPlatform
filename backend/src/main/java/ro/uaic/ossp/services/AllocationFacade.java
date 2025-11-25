// src/main/java/ro/uaic/ossp/services/AllocationFacade.java
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

        AllocationStrategy strategy = null;
        if (algorithm != null && !algorithm.isBlank()) {
            try {
                strategy = AllocationStrategy.fromValue(algorithm);  // Changed from valueOf()
            } catch (IllegalArgumentException ignored) {
                // Log or handle unknown strategy
                strategy = AllocationStrategy.GALE_SHAPLEY; // Default strategy
            }
        }

        return allocationService.executeAllocation(prefs, strategy);
    }
}