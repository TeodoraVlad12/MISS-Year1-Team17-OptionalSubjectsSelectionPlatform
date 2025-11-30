package ro.uaic.ossp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ro.uaic.ossp.models.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    @Query(value = "SELECT * FROM grades WHERE student_id IN (:studentIds) AND year = :year AND specialization = :specialization", nativeQuery = true)
    List<Grade> findByStudentIdInAndYearAndSpecialization(@Param("studentIds") List<Long> studentIds,
                                                          @Param("year") int year,
                                                          @Param("specialization") String specialization);

    @Query(value = "SELECT * FROM grades WHERE student_id IN (:studentIds)", nativeQuery = true)
    List<Grade> findByStudentIdIn(@Param("studentIds") List<Long> studentIds);
}