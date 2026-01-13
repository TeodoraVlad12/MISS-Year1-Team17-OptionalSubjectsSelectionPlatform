package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionalCourseService {

    @Autowired
    private StudentRepository studentRepository;

    public List<OptionalCourse> getOptionalsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        int year = student.getAcademicYear();
        String specialization = student.getSpecialization();

        return studentRepository.findOptionalsForYearAndSpecialization(year, specialization);
    }
}
