package ro.uaic.ossp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.LoginRequestDTO;
import ro.uaic.ossp.dtos.LoginResponseDTO;
import ro.uaic.ossp.dtos.RegisterRequestDTO;
import ro.uaic.ossp.dtos.RegisterResponseDTO;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.models.enums.UserRole;
import ro.uaic.ossp.monitor.RegistrationMonitor;
import ro.uaic.ossp.repositories.StudentRepository;
import ro.uaic.ossp.services.AuthService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationMonitor registrationMonitor;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(
                    new RegisterResponseDTO(false, "Email and password are required", Collections.emptyList()));
        }

        if (studentRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(
                    new RegisterResponseDTO(false, "Email already in use", Collections.emptyList()));
        }

        if (req.getMatriculationNumber() != null && studentRepository.existsByMatriculationNumber(req.getMatriculationNumber())) {
            return ResponseEntity.badRequest().body(
                    new RegisterResponseDTO(false, "Matriculation number already in use", Collections.emptyList()));
        }

        Student student = new Student();
        student.setEmail(req.getEmail());
        student.setPassword(passwordEncoder.encode(req.getPassword()));
        student.setFirstName(req.getFirstName());
        student.setLastName(req.getLastName());
        student.setMatriculationNumber(req.getMatriculationNumber());
        student.setAcademicYear(req.getAcademicYear());
        student.setSpecialization(req.getSpecialization());
        student.setGroupNumber(req.getGroupNumber());

        student.setRole(UserRole.STUDENT);

        try {
            studentRepository.save(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new RegisterResponseDTO(false, "Failed to save student: " + e.getMessage(), Collections.emptyList()));
        }

        List<String> monitorMessages;
        try {
            monitorMessages = registrationMonitor.checkRegistration(student);
        } catch (Exception e) {
            monitorMessages = Collections.singletonList("Monitor execution failed: " + e.getMessage());
        }

        return ResponseEntity.ok(new RegisterResponseDTO(true, "Registration successful", monitorMessages));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean isValid = authService.validateToken(token);
                if (isValid) {
                    return ResponseEntity.ok().body("{\"valid\": true}");
                }
            }
            return ResponseEntity.badRequest().body("{\"valid\": false}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"valid\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }
}