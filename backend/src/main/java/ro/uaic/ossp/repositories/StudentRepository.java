package ro.uaic.ossp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.ossp.models.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByMatriculationNumber(String matriculationNumber);
    Optional<Student> findByEmail(String email);

    List<Student> findByAcademicYearAndSpecialization(Integer academicYear, String specialization);
}