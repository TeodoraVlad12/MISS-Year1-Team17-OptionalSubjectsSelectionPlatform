package com.example.ossp;

import com.example.ossp.dtos.AllocationParameters;
import com.example.ossp.entities.OptionalCourse;
import com.example.ossp.entities.Preference;
import com.example.ossp.entities.Student;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllocationContext {
    private final List<Student> students;
    private final List<OptionalCourse> subjects;
    private final List<Preference> preferences;
    private final AllocationParameters parameters;
}