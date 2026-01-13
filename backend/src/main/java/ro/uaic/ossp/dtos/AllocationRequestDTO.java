package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.AllocationStrategy;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationRequestDTO {
    private List<PreferenceDTO> preferences;
    private AllocationStrategy allocationStrategy;

    private Boolean runDemo;  // true = demo, false = realest allocation
    private DemoType demoType;
    private Integer demoStudentCount;

    public enum DemoType {
        QUICK,
        INTEGRATED
    }
}