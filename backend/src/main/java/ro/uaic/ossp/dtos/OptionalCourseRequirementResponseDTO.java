package ro.uaic.ossp.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptionalCourseRequirementResponseDTO {
    private Long id;
    private Long mandatoryId;
    private String mandatoryName;
    private double percentage;
}
