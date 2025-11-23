package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.UserRole;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    
    private String token;
    private UserRole role;
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String matriculationNumber;
    private Integer academicYear;
    private String specialization;
    private String groupNumber;
}
