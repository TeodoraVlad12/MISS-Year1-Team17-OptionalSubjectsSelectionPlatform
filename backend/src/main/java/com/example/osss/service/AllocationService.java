package com.example.osss.service;

import com.example.osss.AllocationContext;
import com.example.osss.dto.AllocationResult;
import com.example.osss.factory.AllocationStrategyFactory;
import com.example.osss.strategy.allocation.AllocationStrategy;
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