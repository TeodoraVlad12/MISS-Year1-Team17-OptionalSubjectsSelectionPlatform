package com.example.ossp.strategies.allocations;

import com.example.ossp.AllocationContext;
import com.example.ossp.dtos.AllocationResult;

public abstract class AbstractAllocationStrategy implements AllocationStrategy {

    // TEMPLATE METHOD
    @Override
    public final AllocationResult executeAllocation(AllocationContext context) {
        validateInput(context);
        preProcess(context);
        AllocationResult result = performAllocation(context);
        postProcess(context, result);
        return result;
    }

    // operations with default implementations
    protected void validateInput(AllocationContext context) {
        // validation logic
        if (context == null) {
            throw new IllegalArgumentException("AllocationContext cannot be null");
        }
    }

    protected void preProcess(AllocationContext context) {
        // preprocessing
        System.out.println("Pre-processing allocation for " + getStrategyName());
    }

    // primitive operation - must be implemented by subclasses
    protected abstract AllocationResult performAllocation(AllocationContext context);

    protected void postProcess(AllocationContext context, AllocationResult result) {
        // postprocessing
        System.out.println("Post-processing allocation results");
    }
}