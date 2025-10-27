package com.example.osss;

import com.example.osss.dto.AllocationParameters;
import com.example.osss.entity.OptionalSubject;
import com.example.osss.entity.Preference;
import com.example.osss.entity.Student;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllocationContext {
    private final List<Student> students;
    private final List<OptionalSubject> subjects;
    private final List<Preference> preferences;
    private final AllocationParameters parameters;
}