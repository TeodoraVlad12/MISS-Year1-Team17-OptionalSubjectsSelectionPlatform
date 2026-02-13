package ro.uaic.ossp.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoResultDTO {
    private String scenarioName;
    private int totalStudents;
    private int totalMandatoryCourses;
    private int totalOptionalCourses;
    private int generatedPreferences;
    private int allocatedStudents;
    private long executionTimeMs;
    private Map<Integer, Long> preferenceRankDistribution;
    private Map<String, Long> courseAllocationStats;
    private List<AllocationDetailDTO> allocationDetails;
    private List<StudentDemoDTO> studentSamples;

    @Data
    @Builder
    public static class AllocationDetailDTO {
        private Long studentId;
        private String studentName;
        private Long courseId;
        private String courseName;
        private Integer preferenceRank;
        private Double studentAverage;
    }

    @Data
    @Builder
    public static class StudentDemoDTO {
        private Long id;
        private String name;
        private String matriculationNumber;
        private String series;
        private Double averageGrade;
        private Map<String, Double> mandatoryCourseGrades;
    }
}