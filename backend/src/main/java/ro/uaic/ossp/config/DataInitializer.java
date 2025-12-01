package ro.uaic.ossp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.CoursePackage;
import ro.uaic.ossp.models.MandatoryCourse;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.repositories.CoursePackageRepository;
import ro.uaic.ossp.repositories.MandatoryCourseRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CoursePackageRepository coursePackageRepository;
    private final OptionalCourseRepository optionalCourseRepository;
    private final MandatoryCourseRepository mandatoryCourseRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeSampleCourses();
    }

    private void initializeSampleCourses() {
        // Check if courses already exist
        if (optionalCourseRepository.count() > 0 || mandatoryCourseRepository.count() > 0) {
            log.info("Courses already exist in database, skipping initialization");
            return;
        }

        log.info("Initializing sample courses for grade upload testing...");

        // Create course package for Computer Science Year 3
        CoursePackage csPackage = CoursePackage.builder()
                .name("Computer Science - Year 3")
                .description("Advanced Computer Science courses for third year students")
                .year(3)
                .semester(1)
                .level("Bachelor")
                .build();
        
        coursePackageRepository.save(csPackage);

        // Create Optional Courses
        OptionalCourse webDev = OptionalCourse.builder()
                .code("CS301")
                .maxStudents(50)
                .coursePackage(csPackage)
                .build();
        webDev.setName("Web Development");

        OptionalCourse mobileDev = OptionalCourse.builder()
                .code("CS302")
                .maxStudents(45)
                .coursePackage(csPackage)
                .build();
        mobileDev.setName("Mobile Applications");

        OptionalCourse databases = OptionalCourse.builder()
                .code("CS303")
                .maxStudents(60)
                .coursePackage(csPackage)
                .build();
        databases.setName("Database Systems");

        OptionalCourse softwareEng = OptionalCourse.builder()
                .code("CS304")
                .maxStudents(55)
                .coursePackage(csPackage)
                .build();
        softwareEng.setName("Software Engineering");

        // Save Optional Courses
        optionalCourseRepository.save(webDev);
        optionalCourseRepository.save(mobileDev);
        optionalCourseRepository.save(databases);
        optionalCourseRepository.save(softwareEng);

        // Create some Mandatory Courses for testing
        MandatoryCourse algorithms = MandatoryCourse.builder()
                .code("CS201")
                .build();
        algorithms.setName("Algorithms");

        MandatoryCourse mathematics = MandatoryCourse.builder()
                .code("MATH201")
                .build();
        mathematics.setName("Mathematics");

        MandatoryCourse physics = MandatoryCourse.builder()
                .code("PHYS201")
                .build();
        physics.setName("Physics");

        // Save Mandatory Courses
        mandatoryCourseRepository.save(algorithms);
        mandatoryCourseRepository.save(mathematics);
        mandatoryCourseRepository.save(physics);

        log.info("Sample courses initialized successfully!");
        log.info("Optional courses: Web Development, Mobile Applications, Database Systems, Software Engineering");
        log.info("Mandatory courses: Algorithms, Mathematics, Physics");
    }
}
