package ro.uaic.ossp.dtos;

import lombok.Data;

@Data
public class OptionalCourseRequirementDTO {
    private Long mandatoryCourseId;
    private double percentage;
}
