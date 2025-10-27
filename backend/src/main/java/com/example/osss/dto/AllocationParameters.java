package com.example.osss.dto;

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