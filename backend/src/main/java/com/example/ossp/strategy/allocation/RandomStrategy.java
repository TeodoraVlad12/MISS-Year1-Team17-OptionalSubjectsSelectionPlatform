package com.example.ossp.strategy.allocation;

import com.example.ossp.AllocationContext;
import com.example.ossp.dto.AllocationResult;
import com.example.ossp.entity.Student;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("randomStrategy")
public class RandomStrategy extends AbstractAllocationStrategy {

    @Override
    protected AllocationResult performAllocation(AllocationContext context) {
        System.out.println("Executing random allocation...");

        Map<Student, OptionalSubject> assignments = new HashMap<>();
        List<Student> students = context.getStudents();
        List<OptionalSubject> subjects = context.getSubjects();

        // Simple random assignment
        for (int i = 0; i < Math.min(students.size(), subjects.size()); i++) {
            assignments.put(students.get(i), subjects.get(i));
        }

        AllocationResult result = new AllocationResult();
        result.setAssignments(assignments);
        return result;
    }

    @Override
    public String getStrategyName() {
        return "RANDOM";
    }
}