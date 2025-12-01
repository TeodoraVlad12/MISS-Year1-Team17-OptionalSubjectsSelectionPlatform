package ro.uaic.ossp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.ossp.models.MandatoryCourse;

import java.util.Optional;

public interface MandatoryCourseRepository extends JpaRepository<MandatoryCourse, Long> {
    Optional<MandatoryCourse> findByNameIgnoreCase(String name);
}
