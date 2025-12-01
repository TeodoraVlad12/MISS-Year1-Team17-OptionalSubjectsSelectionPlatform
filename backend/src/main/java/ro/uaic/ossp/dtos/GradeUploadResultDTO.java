package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GradeUploadResultDTO {
    private int totalRows;
    private int inserted;
    private int updated;
    private int skipped;
    private String message; // Optional message for additional info
}