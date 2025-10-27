package com.example.osss.strategy.allocation;

import com.example.osss.AllocationContext;
import com.example.osss.dto.AllocationResult;

public interface AllocationStrategy {
    AllocationResult executeAllocation(AllocationContext context);
    String getStrategyName();
}