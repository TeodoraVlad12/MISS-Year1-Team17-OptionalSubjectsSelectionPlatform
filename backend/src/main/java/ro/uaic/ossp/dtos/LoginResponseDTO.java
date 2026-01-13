package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.UserRole;

@Data
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

    public static class LoginResponseDTOBuilder {
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

        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder token(String token) { this.token = token; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder role(UserRole role) { this.role = role; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder userId(Long userId) { this.userId = userId; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder email(String email) { this.email = email; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder matriculationNumber(String matriculationNumber) { this.matriculationNumber = matriculationNumber; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder academicYear(Integer academicYear) { this.academicYear = academicYear; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder groupNumber(String groupNumber) { this.groupNumber = groupNumber; return this; }

        public ro.uaic.ossp.dtos.LoginResponseDTO build() {
            return new ro.uaic.ossp.dtos.LoginResponseDTO(token, role, userId, email, firstName, lastName, matriculationNumber, academicYear, specialization, groupNumber);
        }
    }

    public static ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder builder() {
        return new ro.uaic.ossp.dtos.LoginResponseDTO.LoginResponseDTOBuilder();
    }
}
