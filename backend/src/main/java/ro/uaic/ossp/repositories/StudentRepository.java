package ro.uaic.ossp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.Student;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByMatriculationNumber(String matriculationNumber);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByStudentNumber(String studentNumber);

    @Query("""
        SELECT oc FROM OptionalCourse oc
        WHERE oc.coursePackage.year = :year
        AND oc.coursePackage.level = :specialization
    """)
    List<OptionalCourse> findOptionalsForYearAndSpecialization(int year, String specialization);
}
