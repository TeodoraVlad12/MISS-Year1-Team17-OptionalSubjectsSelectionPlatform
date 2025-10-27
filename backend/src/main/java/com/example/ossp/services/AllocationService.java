package com.example.ossp.services;

import com.example.ossp.AllocationContext;
import com.example.ossp.dtos.AllocationResult;
import com.example.ossp.factories.AllocationStrategyFactory;
import com.example.ossp.strategies.allocations.AllocationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AllocationService {
    private final AllocationStrategyFactory strategyFactory;

    public AllocationResult executeAllocation(AllocationContext context, String algorithmType) {
        AllocationStrategy strategy = strategyFactory.getStrategy(algorithmType);
        return strategy.executeAllocation(context);
    }

    public void printAvailableStrategies() {
        System.out.println("Available allocation strategies:");
        System.out.println("- GALE_SHAPLEY");
        System.out.println("- GRADE_BASED");
        System.out.println("- RANDOM");
    }
}