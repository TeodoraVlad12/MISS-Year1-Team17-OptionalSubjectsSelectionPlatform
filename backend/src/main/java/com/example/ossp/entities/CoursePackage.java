package com.example.ossp.entities;

import com.example.ossp.prototype.Prototype;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "course_packages")
public class CoursePackage implements Prototype<CoursePackage> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int year;        // 1, 2, 3, ...

    @Column(nullable = false)
    private int semester;    // 1 or 2

    @Column(nullable = false)
    private String level; // Bachelor, Master, etc.

    @OneToMany(mappedBy = "coursePackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionalCourse> optionalCourses = new ArrayList<>();

    /**
     * Creates a deep copy of this CoursePackage.
     * This performs a deep clone including all nested OptionalCourses.
     * The clone will have null ID (for new persistence) and all courses will be cloned.
     * 
     * Use cases:
     * - Creating a package template for a new semester/year
     * - Duplicating successful package configurations
     * - Creating test data based on existing packages
     * 
     * @return A new CoursePackage instance with all properties and courses cloned
     */
    @Override
    public CoursePackage clone() {
        CoursePackage cloned = new CoursePackage();
        cloned.setId(null); // New entity should get a new ID
        cloned.setName(this.name);
        cloned.setDescription(this.description);
        cloned.setYear(this.year);
        cloned.setSemester(this.semester);
        cloned.setLevel(this.level);
        
        // Deep clone the optional courses
        List<OptionalCourse> clonedCourses = this.optionalCourses.stream()
            .map(course -> {
                OptionalCourse clonedCourse = course.clone();
                clonedCourse.setCoursePackage(cloned); // Set the bidirectional relationship
                return clonedCourse;
            })
            .collect(Collectors.toList());
        
        cloned.setOptionalCourses(clonedCourses);
        
        return cloned;
    }
}
