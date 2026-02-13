package ro.uaic.ossp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.uaic.ossp.models.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);

    boolean existsByMatriculationNumber(String matriculationNumber);

    List<Student> findByAcademicYearAndSpecialization(Integer academicYear, String specialization);

    List<Student> findByAcademicYearAndSpecializationAndGroupNumber(Integer academicYear, String specialization, String groupNumber);

    long countByAcademicYearAndSpecializationAndGroupNumber(Integer academicYear, String specialization, String groupNumber);
}