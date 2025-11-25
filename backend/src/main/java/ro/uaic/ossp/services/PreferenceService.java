package ro.uaic.ossp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.models.Preference;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.OptionalCourseRepository;
import ro.uaic.ossp.repositories.PreferenceRepository;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private OptionalCourseRepository optionalCourseRepository;
    @Autowired
    private PreferenceRepository preferenceRepository;

    @Transactional
    public void savePreferences(List<PreferenceDTO> dtos) {

        if (dtos.isEmpty()) {
            throw new IllegalArgumentException("Preference list cannot be empty");
        }

        Long studentId = dtos.get(0).getStudentId();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Delete old preferences
        preferenceRepository.deleteByStudentId(studentId);

        // Insert new preferences
        List<Preference> newPrefs = dtos.stream()
                .map(dto -> Preference.builder()
                        .priority(dto.getPriority())
                        .student(student)
                        .optionalCourse(optionalCourseRepository.findById(dto.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Optional course not found")))
                        .build()
                ).toList();

        preferenceRepository.saveAll(newPrefs);
    }

    public List<Preference> getPreferencesByStudentId(Long studentId) {
        return preferenceRepository.findByStudentIdOrderByPriority(studentId);
    }

    @Transactional
    public void deletePreferences(Long studentId) {
        preferenceRepository.deleteByStudentId(studentId);
    }
}