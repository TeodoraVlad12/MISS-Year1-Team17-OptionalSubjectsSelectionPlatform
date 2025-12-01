package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.CourseDTO;
import ro.uaic.ossp.repositories.MandatoryCourseRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final OptionalCourseRepository optionalCourseRepository;
    private final MandatoryCourseRepository mandatoryCourseRepository;

    public List<CourseDTO> getAllOptionalCourses() {
        return optionalCourseRepository.findAll().stream()
                .map(course -> CourseDTO.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .code(course.getCode())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getAllMandatoryCourses() {
        return mandatoryCourseRepository.findAll().stream()
                .map(course -> CourseDTO.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .code(course.getCode())
                        .build())
                .collect(Collectors.toList());
    }
}
