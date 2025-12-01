package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.OptionalCourseRepository;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionalCourseService {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private OptionalCourseRepository optionalCourseRepository;

    public List<OptionalCourse> getOptionalsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return optionalCourseRepository.findAll();
    }
}
