package com.example.ossp.strategies.allocations;

import com.example.ossp.AllocationContext;
import com.example.ossp.dtos.AllocationResult;
import com.example.ossp.entities.Student;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("gradeBasedStrategy")
public class GradeBasedStrategy extends AbstractAllocationStrategy {

    @Override
    protected AllocationResult performAllocation(AllocationContext context) {
        System.out.println("Executing grade-based allocation...");

        Map<Student, OptionalSubject> assignments = new HashMap<>();

        // Simplified grade-based allocation
        // TODO: Implement actual grade-based logic
        if (!context.getStudents().isEmpty() && !context.getSubjects().isEmpty()) {
            assignments.put(context.getStudents().get(0), context.getSubjects().get(0));
        }

        AllocationResult result = new AllocationResult();
        result.setAssignments(assignments);
        return result;
    }

    @Override
    public String getStrategyName() {
        return "GRADE_BASED";
    }
}