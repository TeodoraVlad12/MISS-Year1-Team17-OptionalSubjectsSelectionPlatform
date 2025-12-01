package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.OptionalCourseRequirementDTO;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.security.exceptions.NotFoundException;
import ro.uaic.ossp.services.OptionalCourseRequirementService;

import java.util.Map;

@RestController
@RequestMapping("/api/optional/requirements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('SECRETARY')")
public class OptionalCourseRequirementController {

    private final OptionalCourseRequirementService service;

    @GetMapping("/{optionalId}")
    public ResponseEntity<?> getRequirements(@PathVariable Long optionalId) {
        return ResponseEntity.ok(service.getRequirements(optionalId));
    }

    @PostMapping("/{optionalId}")
    public ResponseEntity<?> createRequirement(
            @PathVariable Long optionalId,
            @RequestBody OptionalCourseRequirementDTO dto
    ) {
        try {
            return ResponseEntity.ok(service.createRequirement(optionalId, dto));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequirement(
            @PathVariable Long id,
            @RequestBody OptionalCourseRequirementDTO dto
    ) {
        try {
            return ResponseEntity.ok(service.updateRequirement(id, dto));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequirement(@PathVariable Long id) {
        try {
            service.deleteRequirement(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
