package ro.uaic.ossp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import ro.uaic.ossp.dtos.AllocationRequestDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.AllocationService;

import java.util.List;

@RestController
@RequestMapping("/api/allocation")
@RequiredArgsConstructor
public class AllocationController {
    private final AllocationService allocationService;

    // Maybe change the path of this endpoint
    @PostMapping("/run")
    public ResponseEntity<List<StudentAllocationDTO>> runAllocation(@Valid @RequestBody AllocationRequestDTO request) {
        try {
            List<StudentAllocationDTO> allocations = allocationService.executeAllocation(
                request.getPreferences(), 
                request.getAllocationStrategy()
            );
            return ResponseEntity.ok(allocations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
