package com.example.ossp.entities;

import com.example.ossp.prototype.Prototype;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "optional_courses")
public class OptionalCourse implements Prototype<OptionalCourse> {
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

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private CoursePackage coursePackage;

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Preference> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Creates a deep copy of this OptionalCourse.
     * Note: The clone will have null ID (for new persistence), empty preferences and enrollments,
     * and will need to be associated with a CoursePackage.
     * 
     * @return A new OptionalCourse instance with copied properties
     */
    @Override
    public OptionalCourse clone() {
        OptionalCourse cloned = new OptionalCourse();
        cloned.setId(null); // New entity should get a new ID
        cloned.setName(this.name);
        cloned.setCode(this.code);
        cloned.setMaxStudents(this.maxStudents);
        // Note: coursePackage reference will need to be set by the caller
        cloned.setCoursePackage(null);
        // Don't clone preferences and enrollments - these are relationship-specific
        cloned.setPreferences(new ArrayList<>());
        cloned.setEnrollments(new ArrayList<>());
        return cloned;
    }
}
