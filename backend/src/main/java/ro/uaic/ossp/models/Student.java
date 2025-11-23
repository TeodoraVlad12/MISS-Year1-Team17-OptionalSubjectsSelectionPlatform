package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ro.uaic.ossp.models.enums.UserRole;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
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
}
