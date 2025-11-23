package ro.uaic.ossp.services;

import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.UaicApiResponseDTO;
import ro.uaic.ossp.models.enums.UserRole;

@Service
public class UaicApiService {

    public UaicApiResponseDTO authenticateUser(String email, String password) {
        if (email.endsWith("@student.uaic.ro")) {
            return createMockStudentResponse(email);
        } else if (email.endsWith("@uaic.ro")) {
            return createMockAdminResponse(email);
        } else {
            return null;
        }
    }

    private UaicApiResponseDTO createMockStudentResponse(String email) {
        String[] emailParts = email.split("\\.");
        String matriculationNumber;
        
        if (emailParts.length >= 3) {
            String lastPart = emailParts[emailParts.length - 1].split("@")[0];
            if (lastPart.matches("\\d+")) {
                matriculationNumber = lastPart;
            } else {
                matriculationNumber = "123456";
            }
        } else {
            matriculationNumber = "123456";
        }

        String firstName = capitalizeFirst(emailParts[0]);
        String lastName = emailParts.length > 1 ? 
            capitalizeFirst(emailParts[1].split("@")[0]) : "Student";

        return UaicApiResponseDTO.builder()
                .authenticated(true)
                .email(email)
                .matriculationNumber(matriculationNumber)
                .firstName(firstName)
                .lastName(lastName)
                .academicYear(determineAcademicYear(matriculationNumber))
                .specialization(determineSpecialization(matriculationNumber))
                .groupNumber(determineGroup(matriculationNumber))
                .role(UserRole.STUDENT)
                .build();
    }

    private UaicApiResponseDTO createMockAdminResponse(String email) {
        String[] emailParts = email.split("\\.");
        String firstName = capitalizeFirst(emailParts[0]);
        String lastName = emailParts.length > 1 ? 
            capitalizeFirst(emailParts[1].split("@")[0]) : "Admin";

        return UaicApiResponseDTO.builder()
                .authenticated(true)
                .email(email)
                .matriculationNumber(null)
                .firstName(firstName)
                .lastName(lastName)
                .academicYear(null)
                .specialization(null)
                .groupNumber(null)
                .role(UserRole.ADMIN)
                .build();
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private Integer determineAcademicYear(String matriculationNumber) {
        int lastDigit = Integer.parseInt(matriculationNumber.substring(matriculationNumber.length() - 1));
        return (lastDigit % 3) + 1;
    }

    private String determineSpecialization(String matriculationNumber) {
        if (matriculationNumber.length() >= 2) {
            int digit = Integer.parseInt(matriculationNumber.substring(matriculationNumber.length() - 2, matriculationNumber.length() - 1));
            return switch (digit % 3) {
                case 0 -> "Computer Science";
                case 1 -> "Information Systems";
                case 2 -> "Software Engineering";
                default -> "Computer Science";
            };
        }
        return "Computer Science";
    }

    private String determineGroup(String matriculationNumber) {
        if (matriculationNumber.length() > 10) {
            int groupNum = Integer.parseInt(matriculationNumber.substring(0, 2)) % 10;
            return "A" + (groupNum + 1);
        }
        return "2";
    }

}
