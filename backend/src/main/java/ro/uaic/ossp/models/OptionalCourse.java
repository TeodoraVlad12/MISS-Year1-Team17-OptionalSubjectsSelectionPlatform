package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "optional_courses")
@DiscriminatorValue("OPTIONAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionalCourse extends CourseBase {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "max_students", nullable = false)
    private int maxStudents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private CoursePackage coursePackage;

    @OneToMany(mappedBy = "optionalCourse",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Preference> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
}