package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.LoginResponseDTO;
import ro.uaic.ossp.models.User;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.UserRepository;
import ro.uaic.ossp.repositories.StudentRepository;
import ro.uaic.ossp.security.JwtTokenUtil;
import ro.uaic.ossp.dtos.UaicApiResponseDTO;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UaicApiService uaicApiService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserFactory userFactory; // NEW: Factory pattern

    public LoginResponseDTO authenticate(String email, String password) {
        UaicApiResponseDTO apiResponse = uaicApiService.authenticateUser(email, password);

        validateApiResponse(apiResponse); // EXTRACT METHOD

        User user = findOrCreateUser(apiResponse);
        String token = generateUserToken(user); // EXTRACT METHOD

        return buildLoginResponse(user, token); // EXTRACT METHOD
    }

    private void validateApiResponse(UaicApiResponseDTO apiResponse) {
        if (apiResponse == null || !apiResponse.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }
    }

    private User findOrCreateUser(UaicApiResponseDTO apiResponse) {
        return userRepository.findByEmail(apiResponse.getEmail())
                .map(existingUser -> updateUser(existingUser, apiResponse)) // EXTRACT METHOD
                .orElseGet(() -> createNewUser(apiResponse)); // EXTRACT METHOD
    }

    private User updateUser(User user, UaicApiResponseDTO apiResponse) {
        user.setFirstName(apiResponse.getFirstName());
        user.setLastName(apiResponse.getLastName());

        if (user instanceof Student student) {
            updateStudentData(student, apiResponse); // EXTRACT METHOD
        }

        return userRepository.save(user);
    }

    private void updateStudentData(Student student, UaicApiResponseDTO apiResponse) {
        student.setMatriculationNumber(apiResponse.getMatriculationNumber());
        student.setAcademicYear(apiResponse.getAcademicYear());
        student.setSpecialization(apiResponse.getSpecialization());
        student.setGroupNumber(apiResponse.getGroupNumber());
    }

    private User createNewUser(UaicApiResponseDTO apiResponse) {
        User user = userFactory.createUser(apiResponse); // REPLACE TYPE CODE WITH STRATEGY
        return userRepository.save(user);
    }

    private String generateUserToken(User user) {
        return jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
    }

    private LoginResponseDTO buildLoginResponse(User user, String token) {
        LoginResponseDTO.LoginResponseDTOBuilder builder = LoginResponseDTO.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName());

        if (user instanceof Student student) {
            addStudentFields(builder, student); // EXTRACT METHOD
        }

        return builder.build();
    }

    private void addStudentFields(LoginResponseDTO.LoginResponseDTOBuilder builder, Student student) {
        builder.matriculationNumber(student.getMatriculationNumber())
                .academicYear(student.getAcademicYear())
                .specialization(student.getSpecialization())
                .groupNumber(student.getGroupNumber());
    }

    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }
}

// NEW: Factory class to handle user creation
@Component
@RequiredArgsConstructor
class UserFactory {
    public User createUser(UaicApiResponseDTO apiResponse) {
        return switch (apiResponse.getRole()) {
            case STUDENT -> createStudent(apiResponse);
            default -> createRegularUser(apiResponse);
        };
    }

    private Student createStudent(UaicApiResponseDTO apiResponse) {
        return Student.builder()
                .email(apiResponse.getEmail())
                .firstName(apiResponse.getFirstName())
                .lastName(apiResponse.getLastName())
                .role(apiResponse.getRole())
                .matriculationNumber(apiResponse.getMatriculationNumber())
                .academicYear(apiResponse.getAcademicYear())
                .specialization(apiResponse.getSpecialization())
                .groupNumber(apiResponse.getGroupNumber())
                .build();
    }

    private User createRegularUser(UaicApiResponseDTO apiResponse) {
        return User.builder()
                .email(apiResponse.getEmail())
                .firstName(apiResponse.getFirstName())
                .lastName(apiResponse.getLastName())
                .role(apiResponse.getRole())
                .build();
    }
}