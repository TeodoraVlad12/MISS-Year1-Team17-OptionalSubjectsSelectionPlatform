package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.dtos.TransferRequestCreateDTO;
import ro.uaic.ossp.dtos.TransferRequestResponseDTO;
import ro.uaic.ossp.dtos.TransferRequestUpdateDTO;
import ro.uaic.ossp.models.enums.TransferStatus;
import ro.uaic.ossp.services.TransferRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/transfer-requests")
@RequiredArgsConstructor
public class TransferRequestController {

    private final TransferRequestService service;

    @PostMapping
    public ResponseEntity<TransferRequestResponseDTO> create(@RequestBody TransferRequestCreateDTO dto) {
        return ResponseEntity.ok(
                service.create(dto.getStudentId(), dto.getCurrentCourseId(), dto.getRequestedCourseId())
        );
    }

    @GetMapping
    public List<TransferRequestResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TransferRequestResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransferRequestResponseDTO> update(@PathVariable Long id,
                                                             @RequestBody TransferRequestUpdateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto.getRequestedCourseId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public List<TransferRequestResponseDTO> getByStudent(@PathVariable Long studentId) {
        return service.getByStudent(studentId);
    }

    @GetMapping("/status/{status}")
    public List<TransferRequestResponseDTO> getByStatus(@PathVariable TransferStatus status) {
        return service.getByStatus(status);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TransferRequestResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(service.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TransferRequestResponseDTO> reject(@PathVariable Long id) {
        return ResponseEntity.ok(service.reject(id));
    }
}
