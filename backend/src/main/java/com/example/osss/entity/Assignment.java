package com.example.osss.entity;

import com.example.osss.model.enums.AssignmentType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private OptionalSubject subject;

    private AssignmentType type;
    private Date assignmentDate;

    // constructors, getters, setters
}