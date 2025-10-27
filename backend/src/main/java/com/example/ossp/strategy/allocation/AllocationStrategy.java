package com.example.ossp.strategy.allocation;

import com.example.ossp.AllocationContext;
import com.example.ossp.dto.AllocationResult;

public interface AllocationStrategy {
    AllocationResult executeAllocation(AllocationContext context);
    String getStrategyName();
}