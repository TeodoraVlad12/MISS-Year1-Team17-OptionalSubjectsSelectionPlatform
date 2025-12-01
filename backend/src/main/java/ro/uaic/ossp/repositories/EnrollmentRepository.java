package ro.uaic.ossp.repositories;

import ro.uaic.ossp.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByOptionalCourseId(Long courseId);
    Optional<Enrollment> findByStudentIdAndOptionalCourseId(Long studentId, Long courseId);
}
