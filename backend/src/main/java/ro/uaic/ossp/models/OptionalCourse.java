package ro.uaic.ossp.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "optional_courses")
public class OptionalCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "max_students", nullable = false)
    private int maxStudents;

    @Column(name = "package_id", insertable = false, updatable = false)
    private Long packageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    @JsonIgnore // breaks circular dependency when json serializing through api endpoint
    private CoursePackage coursePackage;

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // breaks circular dependency when json serializing through api endpoint
    private List<Preference> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // breaks circular dependency when json serializing through api endpoint
    private List<Enrollment> enrollments = new ArrayList<>();
}
