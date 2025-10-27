package com.example.osss.decorator;

import com.example.osss.AllocationContext;
import com.example.osss.dto.AllocationResult;
import com.example.osss.strategy.allocation.AllocationStrategy;
import org.springframework.stereotype.Component;

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