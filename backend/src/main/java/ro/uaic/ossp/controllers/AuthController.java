package ro.uaic.ossp.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.LoginRequestDTO;
import ro.uaic.ossp.dtos.LoginResponseDTO;
import ro.uaic.ossp.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Authentication failed: " + e.getMessage());
        }
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
