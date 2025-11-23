package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "students")
public class Student {
    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @MapsId
    private User user;

    @Column(name = "matriculation_number", unique = true)
    private String matriculationNumber;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "group_number")
    private String groupNumber;
}
