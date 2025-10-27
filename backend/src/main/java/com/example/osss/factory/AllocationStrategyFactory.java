package com.example.osss.factory;

import com.example.osss.decorator.LoggingAllocationDecorator;
import com.example.osss.decorator.ValidationAllocationDecorator;
import com.example.osss.strategy.allocation.AllocationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AllocationStrategyFactory {
    private final Map<String, AllocationStrategy> strategies;

    @Autowired
    public AllocationStrategyFactory(
            @Qualifier("galeShapleyStrategy") AllocationStrategy galeShapley,
            @Qualifier("gradeBasedStrategy") AllocationStrategy gradeBased,
            @Qualifier("randomStrategy") AllocationStrategy random
    ) {
        // decorators to create decorated strategies
        strategies = Map.of(
                "GALE_SHAPLEY", createDecoratedStrategy(galeShapley),
                "GRADE_BASED", createDecoratedStrategy(gradeBased),
                "RANDOM", createDecoratedStrategy(random)
        );
    }

    private AllocationStrategy createDecoratedStrategy(AllocationStrategy strategy) {
        // decorators in sequence: Validation -> Logging -> Original Strategy
        return new ValidationAllocationDecorator(
                new LoggingAllocationDecorator(strategy)
        );
    }

    public AllocationStrategy getStrategy(String algorithmType) {
        AllocationStrategy strategy = strategies.get(algorithmType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown algorithm type: " + algorithmType);
        }
        return strategy;
    }
}