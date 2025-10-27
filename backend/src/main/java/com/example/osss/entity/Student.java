package com.example.osss.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Student {
    @Id
    private String matriculationNumber;
    private String name;
    private String email;
    private Double averageGrade;
    private String academicYearSemester;

    @OneToMany(mappedBy = "student")
    private List<Preference> preference;

    @OneToMany(mappedBy = "student")
    private List<Assignment> assignments;

    // constructors, getters, setters
}