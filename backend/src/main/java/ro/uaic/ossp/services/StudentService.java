package ro.uaic.ossp.services;

import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.StudentResponseDTO;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.Preference;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.GradeRepository;
import ro.uaic.ossp.repositories.PreferenceRepository;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PreferenceRepository preferenceRepository;
    private final GradeRepository gradeRepository;

    public StudentService(StudentRepository studentRepository,
                          PreferenceRepository preferenceRepository,
                          GradeRepository gradeRepository) {
        this.studentRepository = studentRepository;
        this.preferenceRepository = preferenceRepository;
        this.gradeRepository = gradeRepository;
    }

    public List<StudentResponseDTO> getFor(Integer year, String specialization) {
        List<Student> students = studentRepository.findByAcademicYearAndSpecialization(year, specialization);

        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        // load grades and group by student id
        List<Grade> grades = gradeRepository.findByStudentIdInAndYearAndSpecialization(studentIds, year, specialization);
        Map<Long, List<Grade>> gradesByStudent = grades.stream().collect(Collectors.groupingBy(Grade::getStudentId));

        // preferences for all students
        List<Preference> allPrefs = preferenceRepository.findByStudentIdIn(studentIds);
        Map<Long, List<Preference>> prefsByStudent = allPrefs.stream().collect(Collectors.groupingBy(p -> p.getStudent().getId()));

        List<StudentResponseDTO> result = new ArrayList<>();
        for (Student s : students) {
            StudentResponseDTO dto = new StudentResponseDTO();
            dto.setId(s.getId());
            dto.setFullName(s.getFullName());

            List<Preference> prefs = prefsByStudent.getOrDefault(s.getId(), Collections.emptyList());
            List<String> prefOptions = prefs.stream()
                    .sorted(Comparator.comparingInt(Preference::getPriority))
                    .map(p -> {
                        if (p.getOptionalCourse() != null) {
                            return (p.getOptionalCourse().getName() != null)
                                    ? p.getOptionalCourse().getName()
                                    : String.valueOf(p.getOptionalCourse().getId());
                        }
                        return "";
                    }).collect(Collectors.toList());
            dto.setPreferences(prefOptions);

            Map<String, Double> gradeMap = new HashMap<>();
            List<Grade> gList = gradesByStudent.getOrDefault(s.getId(), Collections.emptyList());
            for (Grade g : gList) gradeMap.put(g.getSubject(), g.getValue());
            dto.setGrades(gradeMap);

            result.add(dto);
        }
        return result;
    }
}