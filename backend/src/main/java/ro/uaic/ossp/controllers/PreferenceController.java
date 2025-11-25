package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.models.Preference;
import ro.uaic.ossp.services.PreferenceService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class PreferenceController {

    @Autowired
    private PreferenceService preferenceService;

    @PostMapping("/{studentId}/preferences")
    public ResponseEntity<String> savePreferences(
            @PathVariable Long studentId,
            @Validated @RequestBody List<PreferenceDTO> preferences) {

        // Ensure all DTOs have the same studentId
        boolean mismatch = preferences.stream()
                .anyMatch(p -> !p.getStudentId().equals(studentId));

        if (mismatch) {
            return ResponseEntity.badRequest().body("Student ID mismatch in request body");
        }

        preferenceService.savePreferences(preferences);
        return ResponseEntity.ok("Preferences saved successfully");
    }

    @PutMapping("/{studentId}/preferences")
    public ResponseEntity<String> updatePreferences(
            @PathVariable Long studentId,
            @Validated @RequestBody List<PreferenceDTO> preferences) {

        boolean mismatch = preferences.stream()
                .anyMatch(p -> !p.getStudentId().equals(studentId));

        if (mismatch) {
            return ResponseEntity.badRequest().body("Student ID mismatch in request body");
        }

        preferenceService.savePreferences(preferences);
        return ResponseEntity.ok("Preferences updated successfully");
    }

    @GetMapping("/{studentId}/preferences")
    public ResponseEntity<List<Preference>> getPreferences(@PathVariable Long studentId) {
        return ResponseEntity.ok(preferenceService.getPreferencesByStudentId(studentId));
    }

    @DeleteMapping("/{studentId}/preferences")
    public ResponseEntity<String> deletePreferences(@PathVariable Long studentId) {
        preferenceService.deletePreferences(studentId);
        return ResponseEntity.ok("Preferences deleted successfully");
    }

}
