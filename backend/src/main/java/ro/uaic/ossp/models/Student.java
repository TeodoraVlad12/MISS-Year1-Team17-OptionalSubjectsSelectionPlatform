package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.UserRole;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {
    
    @Column(name = "matriculation_number", unique = true)
    private String matriculationNumber;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "group_number")
    private String groupNumber;

    @Builder(builderMethodName = "studentBuilder")
    public Student(Long id, String email, String firstName, String lastName, UserRole role, 
                   String department, String position, String matriculationNumber, 
                   Integer academicYear, String specialization, String groupNumber) {
        super(id, email, firstName, lastName, role, department, position);
        this.matriculationNumber = matriculationNumber;
        this.academicYear = academicYear;
        this.specialization = specialization;
        this.groupNumber = groupNumber;
    }
}
