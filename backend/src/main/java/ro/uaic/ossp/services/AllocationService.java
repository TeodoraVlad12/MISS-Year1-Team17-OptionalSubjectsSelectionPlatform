package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.models.enums.AllocationStrategy;
import ro.uaic.ossp.services.factories.AllocationStrategyFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllocationService {
    private final AllocationStrategyFactory strategyFactory;

    public List<StudentAllocationDTO> executeAllocation(List<PreferenceDTO> preferences, AllocationStrategy strategy) {
        return strategyFactory.getStrategy(strategy).executeAllocation(preferences);
    }
}
