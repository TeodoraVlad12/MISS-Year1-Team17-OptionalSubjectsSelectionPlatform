package ro.uaic.ossp.repositories;

import ro.uaic.ossp.models.CoursePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursePackageRepository extends JpaRepository<CoursePackage, Long> {
    
    List<CoursePackage> findByYearAndSemesterAndLevel(int year, int semester, String level);
    
    List<CoursePackage> findByLevel(String level);
}
