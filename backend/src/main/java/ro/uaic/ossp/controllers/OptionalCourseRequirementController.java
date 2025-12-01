package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.OptionalCourseRequirementDTO;
import ro.uaic.ossp.services.OptionalCourseRequirementService;

@RestController
@RequestMapping("/api/optional/requirements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
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
        return ResponseEntity.ok(service.createRequirement(optionalId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequirement(
            @PathVariable Long id,
            @RequestBody OptionalCourseRequirementDTO dto
    ) {
        return ResponseEntity.ok(service.updateRequirement(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequirement(@PathVariable Long id) {
        service.deleteRequirement(id);
        return ResponseEntity.noContent().build();
    }
}
