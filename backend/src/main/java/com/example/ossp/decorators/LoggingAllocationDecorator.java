package com.example.ossp.decorators;

import com.example.ossp.AllocationContext;
import com.example.ossp.dtos.AllocationResult;
import com.example.ossp.strategies.allocations.AllocationStrategy;

public class LoggingAllocationDecorator extends AllocationStrategyDecorator {

    public LoggingAllocationDecorator(AllocationStrategy strategy) {
        super(strategy);
    }

    @Override
    public AllocationResult executeAllocation(AllocationContext context) {
        System.out.println("Starting allocation with: " + getStrategyName());
        AllocationResult result = super.executeAllocation(context);
        System.out.println("Allocation completed successfully");
        return result;
    }
}