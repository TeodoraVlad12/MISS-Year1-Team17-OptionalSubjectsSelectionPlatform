package com.example.osss.decorator;

import com.example.osss.AllocationContext;
import com.example.osss.dto.AllocationResult;
import com.example.osss.strategy.allocation.AllocationStrategy;

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