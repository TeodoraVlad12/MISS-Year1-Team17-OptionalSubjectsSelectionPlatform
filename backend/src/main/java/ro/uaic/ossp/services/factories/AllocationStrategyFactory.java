package ro.uaic.ossp.services.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.uaic.ossp.models.enums.AllocationStrategy;
import ro.uaic.ossp.services.strategies.interfaces.IAllocationStrategy;
import ro.uaic.ossp.services.strategies.implementations.GaleShapleyStrategy;
import ro.uaic.ossp.services.strategies.implementations.GradeBasedStrategy;

@Component
@RequiredArgsConstructor
public class AllocationStrategyFactory {
    private final GaleShapleyStrategy galeShapleyStrategy;
    private final GradeBasedStrategy gradeBasedStrategy;

    public IAllocationStrategy getStrategy(AllocationStrategy strategy) {
        return switch (strategy) {
            case GALE_SHAPLEY -> galeShapleyStrategy;
            case GRADE_BASED -> gradeBasedStrategy;
        };
    }
}