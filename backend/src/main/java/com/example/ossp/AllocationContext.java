package com.example.ossp;

import com.example.ossp.dto.AllocationParameters;
import com.example.ossp.entity.OptionalCourse;
import com.example.ossp.entity.Preference;
import com.example.ossp.entity.Student;
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