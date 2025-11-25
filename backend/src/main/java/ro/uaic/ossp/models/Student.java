package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ro.uaic.ossp.models.enums.UserRole;
import java.util.List;
import java.util.ArrayList;

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

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Preference> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransferRequest> transferRequests = new ArrayList<>();

    // helper to satisfy service code that expects a full name
    public String getFullName() {
        String first = (this.getFirstName() != null) ? this.getFirstName().trim() : "";
        String last  = (this.getLastName()  != null) ? this.getLastName().trim()  : "";
        if (!first.isEmpty() && !last.isEmpty()) return first + " " + last;
        if (!first.isEmpty()) return first;
        if (!last.isEmpty()) return last;
        return String.valueOf(this.getId());
    }
}