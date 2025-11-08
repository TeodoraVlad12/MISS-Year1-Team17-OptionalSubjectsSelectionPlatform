package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentAllocationDTO {
    private Long studentId;
    private String studentName;
    private Long allocatedCourseId;
    private String allocatedCourseName;
    private Integer preferenceRank;
}
