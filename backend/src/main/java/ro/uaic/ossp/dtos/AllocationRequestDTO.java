package ro.uaic.ossp.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.AllocationStrategy;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllocationRequestDTO {
    @NotEmpty(message = "Preferences list cannot be empty")
    @Valid
    private List<PreferenceDTO> preferences;

    @NotNull(message = "Allocation strategy cannot be null")
    private AllocationStrategy allocationStrategy;
}
