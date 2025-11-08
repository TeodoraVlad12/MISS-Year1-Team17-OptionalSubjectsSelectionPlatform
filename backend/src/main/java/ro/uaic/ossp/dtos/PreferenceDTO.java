package ro.uaic.ossp.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenceDTO {
    @NotNull(message = "Priority cannot be null")
    @Min(value = 1, message = "Priority must be at least 1")
    private int priority;

    @NotNull(message = "Student ID cannot be null")
    private Long studentId;

    @NotNull(message = "Optional course ID cannot be null")
    private Long courseId;
}
