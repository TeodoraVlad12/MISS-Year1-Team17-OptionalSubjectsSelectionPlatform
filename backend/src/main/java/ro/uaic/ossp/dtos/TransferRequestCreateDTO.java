package ro.uaic.ossp.dtos;

import lombok.Data;

@Data
public class TransferRequestCreateDTO {
    private Long studentId;
    private Long currentCourseId;
    private Long requestedCourseId;
}
