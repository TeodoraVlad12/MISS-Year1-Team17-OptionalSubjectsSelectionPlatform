package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.services.OptionalCourseService;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class OptionalCourseController {

    private final OptionalCourseService optionalCourseService;

    @GetMapping("/{studentId}/optionals")
    public ResponseEntity<List<OptionalCourse>> getOptionals(@PathVariable Long studentId) {
        try {
            List<OptionalCourse> optionals = optionalCourseService.getOptionalsForStudent(studentId);
            return ResponseEntity.ok(optionals);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.toLowerCase().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
