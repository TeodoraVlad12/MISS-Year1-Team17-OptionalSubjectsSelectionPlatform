package com.example.ossp.decorators;

import com.example.ossp.AllocationContext;
import com.example.ossp.dtos.AllocationResult;
import com.example.ossp.strategies.allocations.AllocationStrategy;

public abstract class AllocationStrategyDecorator implements AllocationStrategy {
    protected AllocationStrategy decoratedStrategy;

    public AllocationStrategyDecorator(AllocationStrategy strategy) {
        this.decoratedStrategy = strategy;
    }

    @Override
    public AllocationResult executeAllocation(AllocationContext context) {
        return decoratedStrategy.executeAllocation(context);
    }

    @Override
    public String getStrategyName() {
        return decoratedStrategy.getStrategyName();
    }
}