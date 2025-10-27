package com.example.ossp.strategies.allocations;

import com.example.ossp.AllocationContext;
import com.example.ossp.dtos.AllocationResult;

public interface AllocationStrategy {
    AllocationResult executeAllocation(AllocationContext context);
    String getStrategyName();
}