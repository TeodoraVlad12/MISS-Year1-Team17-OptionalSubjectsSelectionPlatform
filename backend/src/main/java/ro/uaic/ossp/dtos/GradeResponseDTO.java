package ro.uaic.ossp.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GradeResponseDTO {
    private Long id;
    private String matricol;
    private String studentName;
    private Map<String, Double> gradesByCourseName;
}
