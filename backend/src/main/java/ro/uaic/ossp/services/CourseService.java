package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.models.CourseBase;
import ro.uaic.ossp.models.MandatoryCourse;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.repositories.MandatoryCourseRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final OptionalCourseRepository optionalRepo;
    private final MandatoryCourseRepository mandatoryRepo;

    /**
     * Load courses by a list of course names (case-insensitive) and return a map keyed by lower-cased name.
     */
    public Map<String, CourseBase> loadCourses(List<String> courseNames) {
        Map<String, CourseBase> map = new HashMap<>();
        if (courseNames == null || courseNames.isEmpty()) return map;

        // For each name try optional first then mandatory
        for (String name : courseNames) {
            if (name == null) continue;
            String key = name.toLowerCase(Locale.ROOT).trim();
            if (map.containsKey(key)) continue;
            optionalRepo.findByNameIgnoreCase(name).ifPresentOrElse(
                    oc -> map.put(key, oc),
                    () -> mandatoryRepo.findByNameIgnoreCase(name).ifPresent(mc -> map.put(key, mc))
            );
        }
        return map;
    }
}

