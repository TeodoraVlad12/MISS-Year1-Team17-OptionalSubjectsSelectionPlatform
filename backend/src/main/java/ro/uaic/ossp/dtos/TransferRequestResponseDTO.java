package ro.uaic.ossp.dtos;

import lombok.Builder;
import lombok.Data;
import ro.uaic.ossp.models.enums.TransferStatus;

import java.time.LocalDate;

@Data
@Builder
public class TransferRequestResponseDTO {

    private Long id;
    private Long studentId;
    private Long currentCourseId;
    private Long requestedCourseId;
    private LocalDate requestDate;
    private TransferStatus status;
}
