package ro.uaic.ossp.controllers;

import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.AllocationRequestDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.AllocationFacade;

import java.util.List;

@RestController
@RequestMapping("/api/allocation")
@CrossOrigin(origins = "http://localhost:5173") // allow local frontend during development
public class AllocationController {

    private final AllocationFacade allocationFacade;

    public AllocationController(AllocationFacade allocationFacade) {
        this.allocationFacade = allocationFacade;
    }

    @PostMapping("/run")
    public List<StudentAllocationDTO> runAllocation(@RequestBody(required = false) AllocationRequestDTO req) {
        Integer year = req != null ? req.getYear() : null;
        String specialization = req != null ? req.getSpecialization() : null;
        String algorithm = req != null ? req.getAlgorithm() : null;
        return runAllocationInternal(year, specialization, algorithm);
    }

    private List<StudentAllocationDTO> runAllocationInternal(Integer year, String specialization, String algorithm) {
        int y = (year != null) ? year : 0;
        String spec = (specialization != null) ? specialization : "";
        return allocationFacade.executeAllocationByCriteria(y, spec, algorithm);
    }
}