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
@Table(name = "course_packages")
public class CoursePackage {
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

    @OneToMany(mappedBy = "coursePackage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OptionalCourse> optionalCourses = new ArrayList<>();

}
