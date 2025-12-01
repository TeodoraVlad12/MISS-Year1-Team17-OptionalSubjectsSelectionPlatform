package ro.uaic.ossp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ro.uaic.ossp.services.CourseService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/optional")
    public ResponseEntity<?> getOptionalCourses() {
        return ResponseEntity.ok(courseService.getAllOptionalCourses());
    }

    @GetMapping("/mandatory")
    public ResponseEntity<?> getMandatoryCourses() {
        return ResponseEntity.ok(courseService.getAllMandatoryCourses());
    }
}
