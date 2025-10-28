package com.example.ossp.repositories;

import com.example.ossp.entities.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    
    List<Preference> findByStudentIdOrderByPriority(Long studentId);
    
    List<Preference> findByOptionalCourseId(Long courseId);
}
