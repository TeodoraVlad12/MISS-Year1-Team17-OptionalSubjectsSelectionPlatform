package ro.uaic.ossp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.LoginResponseDTO;
import ro.uaic.ossp.models.User;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.models.enums.UserRole;
import ro.uaic.ossp.repositories.UserRepository;
import ro.uaic.ossp.repositories.StudentRepository;
import ro.uaic.ossp.security.JwtTokenUtil;
import ro.uaic.ossp.dtos.UaicApiResponseDTO;

@Service
public class AuthService {

    @Autowired
    private UaicApiService uaicApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public LoginResponseDTO authenticate(String email, String password) {
        UaicApiResponseDTO apiResponse = uaicApiService.authenticateUser(email, password);
        
        if (apiResponse == null || !apiResponse.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = findOrCreateUser(apiResponse);
        
        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole());

        // Cast to Student if it's a student to get student-specific fields
        Student student = null;
        if (user instanceof Student) {
            student = (Student) user;
        }

        return LoginResponseDTO.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .matriculationNumber(student != null ? student.getMatriculationNumber() : null)
                .academicYear(student != null ? student.getAcademicYear() : null)
                .specialization(student != null ? student.getSpecialization() : null)
                .groupNumber(student != null ? student.getGroupNumber() : null)
                .build();
    }

    private User findOrCreateUser(UaicApiResponseDTO apiResponse) {
        User user = userRepository.findByEmail(apiResponse.getEmail()).orElse(null);

        if (user == null) {
            // Create appropriate user type based on role
            if (apiResponse.getRole() == UserRole.STUDENT) {
                user = Student.builder()
                        .email(apiResponse.getEmail())
                        .firstName(apiResponse.getFirstName())
                        .lastName(apiResponse.getLastName())
                        .role(apiResponse.getRole())
                        .matriculationNumber(apiResponse.getMatriculationNumber())
                        .academicYear(apiResponse.getAcademicYear())
                        .specialization(apiResponse.getSpecialization())
                        .groupNumber(apiResponse.getGroupNumber())
                        .build();
            } else {
                user = User.builder()
                        .email(apiResponse.getEmail())
                        .firstName(apiResponse.getFirstName())
                        .lastName(apiResponse.getLastName())
                        .role(apiResponse.getRole())
                        .build();
            }
            user = userRepository.save(user);
        } else {
            // Update existing user with latest info from UAIC API
            user.setFirstName(apiResponse.getFirstName());
            user.setLastName(apiResponse.getLastName());
            
            // Update student-specific fields if it's a student
            if (user instanceof Student) {
                Student student = (Student) user;
                student.setMatriculationNumber(apiResponse.getMatriculationNumber());
                student.setAcademicYear(apiResponse.getAcademicYear());
                student.setSpecialization(apiResponse.getSpecialization());
                student.setGroupNumber(apiResponse.getGroupNumber());
            }
            
            user = userRepository.save(user);
        }

        return user;
    }

    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }
}
