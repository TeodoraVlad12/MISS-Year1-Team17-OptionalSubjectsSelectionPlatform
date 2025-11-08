package ro.uaic.ossp.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AllocationStrategy {
    GALE_SHAPLEY("gale_shapley", "Gale-Shapley Algorithm"),
    GRADE_BASED("grade_based", "Grade Based Algorithm");

    private final String value;
    private final String displayName;

    AllocationStrategy(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AllocationStrategy fromValue(String value) {
        for (AllocationStrategy strategy : AllocationStrategy.values()) {
            if (strategy.getValue().equalsIgnoreCase(value)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Unknown allocation strategy: " + value);
    }
}
