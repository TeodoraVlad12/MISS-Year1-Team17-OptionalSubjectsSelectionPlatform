package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.UserRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UaicApiResponseDTO {
    private boolean authenticated;
    private String email;
    private String matriculationNumber;
    private String firstName;
    private String lastName;
    private Integer academicYear;
    private String specialization;
    private String groupNumber;
    private UserRole role;
}
