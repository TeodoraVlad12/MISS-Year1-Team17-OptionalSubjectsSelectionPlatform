//package ro.uaic.ossp.dtos;
//
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import ro.uaic.ossp.models.enums.AllocationStrategy;
//
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class AllocationRequestDTO {
//    @NotEmpty(message = "Preferences list cannot be empty")
//    @Valid
//    private List<PreferenceDTO> preferences;
//
//    @NotNull(message = "Allocation strategy cannot be null")
//    private AllocationStrategy allocationStrategy;
//}

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

    // === EXISTING FIELDS ===
    private List<PreferenceDTO> preferences;
    private AllocationStrategy allocationStrategy;

    // === NEW FIELDS FOR DEMO ===
    private Boolean runDemo;  // true = rulează demo, false = rulează cu preferințe custom
    private DemoType demoType; // QUICK sau INTEGRATED
    private Integer demoStudentCount; // Număr studenți pentru demo (opțional)

    public enum DemoType {
        QUICK,      // Demo rapid cu date hardcodate
        INTEGRATED  // Demo complet cu date reale din CSV
    }
}