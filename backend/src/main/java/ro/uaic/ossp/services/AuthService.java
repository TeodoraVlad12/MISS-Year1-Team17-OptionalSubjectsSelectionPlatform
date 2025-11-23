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
        
        Student student = null;
        if (user.getRole() == UserRole.STUDENT) {
            student = studentRepository.findByUser_Id(user.getId()).orElse(null);
        }

        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole());

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
            user = User.builder()
                    .email(apiResponse.getEmail())
                    .firstName(apiResponse.getFirstName())
                    .lastName(apiResponse.getLastName())
                    .role(apiResponse.getRole())
                    .build();

            user = userRepository.save(user);

            if (apiResponse.getRole() == UserRole.STUDENT) {
                Student student = Student.builder()
                        .user(user)
                        .matriculationNumber(apiResponse.getMatriculationNumber())
                        .academicYear(apiResponse.getAcademicYear())
                        .specialization(apiResponse.getSpecialization())
                        .groupNumber(apiResponse.getGroupNumber())
                        .build();
                studentRepository.save(student);
            }
        } else {
            // Update existing user with latest info from UAIC API
            user.setFirstName(apiResponse.getFirstName());
            user.setLastName(apiResponse.getLastName());
            user = userRepository.save(user);

            if (user.getRole() == UserRole.STUDENT) {
                Student student = studentRepository.findByUser_Id(user.getId()).orElse(null);
                if (student != null) {
                    student.setMatriculationNumber(apiResponse.getMatriculationNumber());
                    student.setAcademicYear(apiResponse.getAcademicYear());
                    student.setSpecialization(apiResponse.getSpecialization());
                    student.setGroupNumber(apiResponse.getGroupNumber());
                    studentRepository.save(student);
                }
            }
        }

        return user;
    }

    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }
}
