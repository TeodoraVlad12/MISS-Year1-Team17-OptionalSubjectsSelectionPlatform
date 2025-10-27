package com.example.ossp.repositories;

import com.example.ossp.entities.OptionalCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionalCourseRepository extends JpaRepository<OptionalCourse, Long> {
    
    Optional<OptionalCourse> findByCode(String code);
    
    List<OptionalCourse> findByCoursePackageId(Long packageId);
}
