package ro.uaic.ossp.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.uaic.ossp.models.OptionalCourseRequirement;

import java.util.List;

public interface OptionalCourseRequirementRepository extends JpaRepository<OptionalCourseRequirement, Long> {

    List<OptionalCourseRequirement> findByOptionalCourseId(Long optionalId);

    boolean existsByOptionalCourseIdAndMandatoryCourseId(Long optionalId, Long mandatoryId);
}
