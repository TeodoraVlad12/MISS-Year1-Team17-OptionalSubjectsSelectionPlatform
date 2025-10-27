package com.example.osss.strategy.allocation;

import com.example.osss.AllocationContext;
import com.example.osss.dto.AllocationResult;
import com.example.osss.entity.OptionalSubject;
import com.example.osss.entity.Student;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("galeShapleyStrategy")
public class GaleShapleyStrategy extends AbstractAllocationStrategy {

    @Override
    protected AllocationResult performAllocation(AllocationContext context) {
        System.out.println("Executing Gale-Shapley algorithm...");

        // Simplified Gale-Shapley implementation
        Map<Student, OptionalSubject> assignments = new HashMap<>();

        // TODO: Implement actual Gale-Shapley algorithm
        // This is just a placeholder
        if (!context.getStudents().isEmpty() && !context.getSubjects().isEmpty()) {
            assignments.put(context.getStudents().get(0), context.getSubjects().get(0));
        }

        AllocationResult result = new AllocationResult();
        result.setAssignments(assignments);
        return result;
    }

    @Override
    public String getStrategyName() {
        return "GALE_SHAPLEY";
    }
}