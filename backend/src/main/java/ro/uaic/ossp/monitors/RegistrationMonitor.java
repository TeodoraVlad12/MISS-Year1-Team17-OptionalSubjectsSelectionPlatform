package ro.uaic.ossp.monitors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegistrationMonitor {

    private final StudentRepository studentRepository;
    private final int maxPerGroup;

    public RegistrationMonitor(StudentRepository studentRepository,
                               @Value("${monitor.group.max-per-group:30}") int maxPerGroup) {
        this.studentRepository = studentRepository;
        this.maxPerGroup = maxPerGroup;
    }

    // Synchronously checks simple rules
    public List<String> checkRegistration(Student student) {
        List<String> messages = new ArrayList<>();
        try {
            if (student == null) {
                messages.add("Monitor error: student is null");
                return messages;
            }

            if (student.getAcademicYear() == null || student.getSpecialization() == null || student.getGroupNumber() == null) {
                messages.add("Registration saved; insufficient data to verify group capacity.");
                return messages;
            }

            long count = studentRepository.countByAcademicYearAndSpecializationAndGroupNumber(
                    student.getAcademicYear(), student.getSpecialization(), student.getGroupNumber());

            if (count > maxPerGroup) {
                messages.add("Group capacity exceeded (current=" + count + ", max=" + maxPerGroup + "). Consider requesting transfer.");
            } else {
                messages.add("Registration OK (group size=" + count + ").");
            }
        } catch (Exception e) {
            messages.add("Monitor error: " + e.getMessage());
        }
        return messages;
    }
}