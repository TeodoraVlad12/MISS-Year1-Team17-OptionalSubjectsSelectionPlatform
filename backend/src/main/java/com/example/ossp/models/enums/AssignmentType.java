package com.example.ossp.models.enums;

import lombok.Getter;

@Getter
public enum AssignmentType {
    ALGORITHM("Algorithm", "Assigned by stable matching algorithm", true),
    MANUAL("Manual", "Manually assigned by administrator", true),
    TRANSFER("Transfer", "Assigned via mutual transfer process", false),
    INITIAL("Initial", "Initial system assignment", true),
    CORRECTION("Correction", "Assignment correction", false);

    private final String displayName;
    private final String description;
    private final boolean countsTowardStatistics;

    AssignmentType(String displayName, String description, boolean countsTowardStatistics) {
        this.displayName = displayName;
        this.description = description;
        this.countsTowardStatistics = countsTowardStatistics;
    }

    public boolean isAutomated() {
        return this == ALGORITHM || this == INITIAL;
    }

    public boolean requiresAdminApproval() {
        return this == MANUAL || this == CORRECTION;
    }
}