package ro.uaic.ossp.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.uaic.ossp.models.enums.AllocationStrategy;

import java.util.List;

public class AllocationRequestDTO {
    private Integer year;
    private String specialization;
    private String algorithm;

    public AllocationRequestDTO() {}

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}