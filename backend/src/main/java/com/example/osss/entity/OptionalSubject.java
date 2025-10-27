package com.example.osss.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class OptionalSubject {
    @Id
    private String subjectId;
    private String name;
    private String description;
    private String coordinator;
    private Integer capacity;
    private String targetYearSemester;

    @OneToMany(mappedBy = "subject")
    private List<Preference> preferences;

    @OneToMany(mappedBy = "subject")
    private List<Assignment> assignments;

    // constructors, getters, setters
}