package ro.uaic.ossp.services.strategies.implementations;

import org.springframework.stereotype.Component;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.interfaces.IAllocationStrategy;

import java.util.List;

@Component
public class GradeBasedStrategy implements IAllocationStrategy {
    @Override
    public List<StudentAllocationDTO> executeAllocation(List<PreferenceDTO> preferences) {
        // TODO: Implement Grade-Based allocation algorithm
        return null;
    }
}
