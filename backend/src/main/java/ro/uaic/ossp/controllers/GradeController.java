package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.uaic.ossp.dtos.GradeUploadResultDTO;
import ro.uaic.ossp.services.GradeService;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    /**
     * Upload CSV. Only ADMINs allowed.
     * query param overwrite=true|false (default false)
     */
    @PostMapping("/upload")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GradeUploadResultDTO> uploadGrades(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite
    ) {
        GradeUploadResultDTO result = gradeService.uploadCsv(file, overwrite);
        return ResponseEntity.ok(result);
    }
}