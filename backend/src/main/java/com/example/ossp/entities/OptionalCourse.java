package com.example.ossp.entities;

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

    @ManyToOne
    @JoinColumn(name = "package_id", nullable = false)
    private CoursePackage coursePackage;

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Preference> preferences = new ArrayList<>();

    @OneToMany(mappedBy = "optionalCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();
}
