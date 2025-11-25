package ro.uaic.ossp.controllers;

import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.AllocationFacade;

import java.util.List;
import java.util.Collections;

@RestController
@RequestMapping("/api/allocation")
public class AllocationController {

    private final AllocationFacade allocationFacade;

    public AllocationController(AllocationFacade allocationFacade) {
        this.allocationFacade = allocationFacade;
    }

    @GetMapping("/run")
    public List<StudentAllocationDTO> runAllocation(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String algorithm) {

        int y = (year != null) ? year : 0;
        String spec = (specialization != null) ? specialization : "";
        return allocationFacade.executeAllocationByCriteria(y, spec, algorithm);
    }

    // Kept only to satisfy tests calling runAllocation(null)
    public List<StudentAllocationDTO> runAllocation(Object unused) {
        return runAllocation((Integer) null, null, null);
    }
}