package ro.uaic.ossp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.LoginResponseDTO;
import ro.uaic.ossp.models.User;
import ro.uaic.ossp.repositories.UserRepository;
import ro.uaic.ossp.security.JwtTokenUtil;
import ro.uaic.ossp.dtos.UaicApiResponseDTO;

@Service
public class AuthService {

    @Autowired
    private UaicApiService uaicApiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public LoginResponseDTO authenticate(String email, String password) {
        UaicApiResponseDTO apiResponse = uaicApiService.authenticateUser(email, password);
        
        if (apiResponse == null || !apiResponse.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = findOrCreateUser(apiResponse);

        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole());

        return LoginResponseDTO.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .matriculationNumber(user.getMatriculationNumber())
                .academicYear(user.getAcademicYear())
                .specialization(user.getSpecialization())
                .groupNumber(user.getGroupNumber())
                .build();
    }

    private User findOrCreateUser(UaicApiResponseDTO apiResponse) {
        User user = userRepository.findByEmail(apiResponse.getEmail()).orElse(null);

        if (user == null) {
            user = User.builder()
                    .email(apiResponse.getEmail())
                    .matriculationNumber(apiResponse.getMatriculationNumber())
                    .firstName(apiResponse.getFirstName())
                    .lastName(apiResponse.getLastName())
                    .academicYear(apiResponse.getAcademicYear())
                    .specialization(apiResponse.getSpecialization())
                    .groupNumber(apiResponse.getGroupNumber())
                    .role(apiResponse.getRole())
                    .build();

            user = userRepository.save(user);
        } else {
            // Update existing user with latest info from UAIC API
            user.setFirstName(apiResponse.getFirstName());
            user.setLastName(apiResponse.getLastName());
            user.setAcademicYear(apiResponse.getAcademicYear());
            user.setSpecialization(apiResponse.getSpecialization());
            user.setGroupNumber(apiResponse.getGroupNumber());
            
            user = userRepository.save(user);
        }

        return user;
    }

    public boolean validateToken(String token) {
        return jwtTokenUtil.validateToken(token);
    }
}
