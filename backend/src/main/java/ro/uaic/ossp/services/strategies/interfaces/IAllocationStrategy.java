package ro.uaic.ossp.services.strategies.interfaces;

import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;

import java.util.List;

public interface IAllocationStrategy {
    List<StudentAllocationDTO> executeAllocation(List<PreferenceDTO> preferences);
}
