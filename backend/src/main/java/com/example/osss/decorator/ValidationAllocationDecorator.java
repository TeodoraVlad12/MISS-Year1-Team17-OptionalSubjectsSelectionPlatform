package com.example.osss.decorator;

import com.example.osss.AllocationContext;
import com.example.osss.dto.AllocationResult;
import com.example.osss.strategy.allocation.AllocationStrategy;

public class ValidationAllocationDecorator extends AllocationStrategyDecorator {

    public ValidationAllocationDecorator(AllocationStrategy strategy) {
        super(strategy);
    }

    @Override
    public AllocationResult executeAllocation(AllocationContext context) {
        validateCapacityConstraints(context);
        return super.executeAllocation(context);
    }

    private void validateCapacityConstraints(AllocationContext context) {
        // Basic validation logic
        if (context.getStudents().isEmpty()) {
            throw new IllegalArgumentException("No students provided for allocation");
        }
        if (context.getSubjects().isEmpty()) {
            throw new IllegalArgumentException("No subjects provided for allocation");
        }
        System.out.println("Capacity constraints validated successfully");
    }
}