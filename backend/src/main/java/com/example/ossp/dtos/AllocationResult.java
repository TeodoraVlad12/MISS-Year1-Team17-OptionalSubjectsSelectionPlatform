package com.example.ossp.dtos;

import com.example.ossp.entities.OptionalCourse;
import com.example.ossp.entities.Student;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AllocationResult {
    private Map<Student, OptionalCourse> assignments;
    private List<String> warnings;
    private boolean success;

    public AllocationResult() {
        this.warnings = new ArrayList<>();
        this.success = true;
    }

    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new ArrayList<>();
        }
        this.warnings.add(warning);
    }

    public int getTotalAssignments() {
        return assignments != null ? assignments.size() : 0;
    }
}