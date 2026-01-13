package ro.uaic.ossp.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.*;
import ro.uaic.ossp.models.CourseBase;
import ro.uaic.ossp.models.MandatoryCourse;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.enums.AllocationStrategy;
import ro.uaic.ossp.repositories.MandatoryCourseRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final StudentDataService studentDataService;
    private final MandatoryCourseRepository mandatoryCourseRepository;
    private final OptionalCourseRepository optionalCourseRepository;
    private final AllocationService allocationService;

    public DemoResultDTO runRealJsonDemo() {
        long startTime = System.currentTimeMillis();

        try {
            ClassPathResource resource = new ClassPathResource("demo-preferences.json");
            ObjectMapper mapper = new ObjectMapper();
            DemoJsonData jsonData = mapper.readValue(resource.getInputStream(), DemoJsonData.class);

            List<PreferenceDTO> preferences = jsonData.getPreferences().stream()
                    .map(jsonPref -> {
                        PreferenceDTO dto = new PreferenceDTO();
                        dto.setStudentId(jsonPref.getStudentId());
                        dto.setCourseId(jsonPref.getCourseId());
                        dto.setPriority(jsonPref.getPriority());
                        return dto;
                    })
                    .collect(Collectors.toList());

            List<StudentAllocationDTO> allocations = allocationService.executeAllocation(
                    preferences,
                    AllocationStrategy.GALE_SHAPLEY
            );

            long endTime = System.currentTimeMillis();

            return buildRealDemoResult(jsonData, preferences, allocations, endTime - startTime);

        } catch (Exception e) {
            throw new RuntimeException("Failed to run real JSON demo: " + e.getMessage(), e);
        }
    }

    private DemoResultDTO buildRealDemoResult(DemoJsonData jsonData,
                                              List<PreferenceDTO> preferences,
                                              List<StudentAllocationDTO> allocations,
                                              long executionTime) {

        Map<Long, String> studentNames = jsonData.getPreferences().stream()
                .collect(Collectors.toMap(
                        DemoPreference::getStudentId,
                        DemoPreference::getStudentName,
                        (existing, replacement) -> existing
                ));

        Map<Long, DemoJsonData.CourseInfo> courseInfo = jsonData.getCourses();

        Map<Integer, Long> rankDistribution = allocations.stream()
                .collect(Collectors.groupingBy(
                        StudentAllocationDTO::getPreferenceRank,
                        Collectors.counting()
                ));

        Map<String, Long> courseStats = allocations.stream()
                .collect(Collectors.groupingBy(
                        allocation -> {
                            DemoJsonData.CourseInfo info = courseInfo.get(allocation.getAllocatedCourseId());
                            return info != null ? info.getName() : "Unknown";
                        },
                        Collectors.counting()
                ));

        List<DemoResultDTO.AllocationDetailDTO> allocationDetails = allocations.stream()
                .map(allocation -> {
                    String studentName = studentNames.getOrDefault(allocation.getStudentId(),
                            "Student " + allocation.getStudentId());
                    DemoJsonData.CourseInfo info = courseInfo.get(allocation.getAllocatedCourseId());

                    return DemoResultDTO.AllocationDetailDTO.builder()
                            .studentId(allocation.getStudentId())
                            .studentName(studentName)
                            .courseId(allocation.getAllocatedCourseId())
                            .courseName(info != null ? info.getName() : "Unknown")
                            .preferenceRank(allocation.getPreferenceRank())
                            .studentAverage(8.5)
                            .build();
                })
                .collect(Collectors.toList());

        return DemoResultDTO.builder()
                .scenarioName("Real JSON Demo - Fixed Student Preferences")
                .totalStudents((int) studentNames.keySet().stream().distinct().count())
                .totalMandatoryCourses(0)
                .totalOptionalCourses(courseInfo.size())
                .generatedPreferences(preferences.size())
                .allocatedStudents(allocations.size())
                .executionTimeMs(executionTime)
                .preferenceRankDistribution(rankDistribution)
                .courseAllocationStats(courseStats)
                .allocationDetails(allocationDetails)
                .studentSamples(List.of())
                .build();
    }

    @Data
    private static class DemoJsonData {
        private String scenario;
        private List<DemoPreference> preferences;
        private Map<Long, CourseInfo> courses;

        @Data
        private static class CourseInfo {
            private String name;
            private String code;
            private Integer capacity;
        }
    }

    @Data
    private static class DemoPreference {
        private Long studentId;
        private String studentName;
        private Long courseId;
        private Integer priority;
    }
}