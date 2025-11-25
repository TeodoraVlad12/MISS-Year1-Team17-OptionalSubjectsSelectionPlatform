package ro.uaic.ossp.repositories;

import ro.uaic.ossp.models.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    void deleteByStudentId(Long studentId);
    
    List<Preference> findByStudentIdOrderByPriority(Long studentId);
    
    List<Preference> findByOptionalCourseId(Long courseId);
    List<Preference> findByStudentIdIn(List<Long> studentIds);
}