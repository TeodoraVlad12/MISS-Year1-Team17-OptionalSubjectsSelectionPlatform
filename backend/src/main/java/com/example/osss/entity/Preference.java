package com.example.osss.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Preference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private OptionalSubject subject;

    private Integer rank;  // 1 for first choice, 2 for second choice
    private Date submissionDate;

    // constructors, getters, setters
}