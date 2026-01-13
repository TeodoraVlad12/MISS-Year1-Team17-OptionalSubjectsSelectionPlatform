package ro.uaic.ossp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.AllocationRequestDTO;
import ro.uaic.ossp.dtos.DemoResultDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.models.enums.UserRole;
import ro.uaic.ossp.security.annotations.RequireRole;
import ro.uaic.ossp.services.AllocationService;
import ro.uaic.ossp.services.DemoService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/allocation")
@RequiredArgsConstructor
public class AllocationController {
    private final AllocationService allocationService;
    private final DemoService demoService;

//    @PostMapping("/run")
//    @RequireRole({UserRole.ADMIN, UserRole.SECRETARY})
//    public ResponseEntity<List<StudentAllocationDTO>> runAllocation(@Valid @RequestBody AllocationRequestDTO request) {
//        try {
//            // If preferences are empty, run DEMO but extract only allocations
//            if (request.getPreferences() == null || request.getPreferences().isEmpty()) {
//                DemoResultDTO demoResult = demoService.runIntegratedDemo();
//
//                // Check if allocationDetails is null
//                if (demoResult.getAllocationDetails() == null) {
//                    // Return empty list if no allocations
//                    return ResponseEntity.ok(List.of());
//                }
//
//                // Extract only the allocation list from demo
//                List<StudentAllocationDTO> demoAllocations = demoResult.getAllocationDetails().stream()
//                        .map(detail -> StudentAllocationDTO.builder()
//                                .studentId(detail.getStudentId())
//                                .studentName(detail.getStudentName())
//                                .allocatedCourseId(detail.getCourseId())
//                                .allocatedCourseName(detail.getCourseName())
//                                .preferenceRank(detail.getPreferenceRank())
//                                .build())
//                        .collect(Collectors.toList());
//                return ResponseEntity.ok(demoAllocations);
//            }
//
//            // Normal allocation
//            List<StudentAllocationDTO> allocations = allocationService.executeAllocation(
//                    request.getPreferences(),
//                    request.getAllocationStrategy()
//            );
//            return ResponseEntity.ok(allocations);
//
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    @PostMapping("/run")
    @RequireRole({UserRole.ADMIN, UserRole.SECRETARY})
    public ResponseEntity<List<StudentAllocationDTO>> runAllocation(@Valid @RequestBody AllocationRequestDTO request) {
        try {
            if (request.getPreferences() == null || request.getPreferences().isEmpty()) {
                // JSON Demo
                DemoResultDTO demoResult = demoService.runRealJsonDemo();

                // DemoResultDTO demoResult = demoService.runIntegratedDemo();

                if (demoResult.getAllocationDetails() == null) {
                    return ResponseEntity.ok(List.of());
                }

                List<StudentAllocationDTO> demoAllocations = demoResult.getAllocationDetails().stream()
                        .map(detail -> StudentAllocationDTO.builder()
                                .studentId(detail.getStudentId())
                                .studentName(detail.getStudentName())
                                .allocatedCourseId(detail.getCourseId())
                                .allocatedCourseName(detail.getCourseName())
                                .preferenceRank(detail.getPreferenceRank())
                                .build())
                        .collect(Collectors.toList());
                return ResponseEntity.ok(demoAllocations);
            }

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