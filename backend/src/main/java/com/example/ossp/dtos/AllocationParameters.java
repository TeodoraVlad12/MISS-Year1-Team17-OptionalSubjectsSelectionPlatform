package com.example.ossp.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationParameters {
    private String algorithmType;
    private boolean considerGrades;
    private Integer maxSubjectsPerStudent;
}