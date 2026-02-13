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

//
//    public DemoResultDTO runIntegratedDemo() {
//        long startTime = System.currentTimeMillis();
//
//        List<StudentDataService.StudentRecord> realStudents = studentDataService.loadStudentsFromCsv();
//        List<MandatoryCourse> mandatoryCourses = mandatoryCourseRepository.findAll();
//        List<OptionalCourse> optionalCourses = optionalCourseRepository.findAll();
//
//        if (optionalCourses.isEmpty()) {
//            throw new RuntimeException("No optional courses found in the database for demo!");
//        }
//
//        Map<Long, Map<String, Double>> studentGrades = generateSimulatedGrades(realStudents, mandatoryCourses);
//
//        List<PreferenceDTO> preferences = generateSimulatedPreferences(realStudents, optionalCourses, studentGrades);
//
//        List<StudentAllocationDTO> allocations = allocationService.executeAllocation(
//                preferences,
//                AllocationStrategy.GALE_SHAPLEY
//        );
//
//        long endTime = System.currentTimeMillis();
//
//        return buildDemoResult(
//                realStudents,
//                mandatoryCourses,
//                optionalCourses,
//                studentGrades,
//                preferences,
//                allocations,
//                endTime - startTime
//        );
//    }
//
//    private Map<Long, Map<String, Double>> generateSimulatedGrades(
//            List<StudentDataService.StudentRecord> students,
//            List<MandatoryCourse> mandatoryCourses
//    ) {
//        Random random = new Random(12345); // Fixed seed for consistent results
//        Map<Long, Map<String, Double>> gradesMap = new HashMap<>();
//
//        for (StudentDataService.StudentRecord student : students) {
//            Map<String, Double> grades = new HashMap<>();
//
//            for (MandatoryCourse course : mandatoryCourses) {
//                // Generate grades between 5 and 10, with normal distribution around student average
//                double baseGrade = 6.0 + random.nextDouble() * 4.0; // 6.0 - 10.0
//
//                // Adjust based on student "performance" (name hash)
//                double studentFactor = (student.getName().hashCode() % 100) / 100.0;
//                double finalGrade = Math.min(10.0, Math.max(5.0, baseGrade + studentFactor));
//
//                // Round to 2 decimals
//                finalGrade = Math.round(finalGrade * 100.0) / 100.0;
//
//                grades.put(course.getName(), finalGrade);
//            }
//
//            gradesMap.put(student.getId(), grades);
//        }
//
//        return gradesMap;
//    }
//
//    private List<PreferenceDTO> generateSimulatedPreferences(
//            List<StudentDataService.StudentRecord> students,
//            List<OptionalCourse> optionalCourses,
//            Map<Long, Map<String, Double>> studentGrades
//    ) {
//        Random random = new Random(12345);
//        List<PreferenceDTO> preferences = new ArrayList<>();
//
//        // Calculate each student's average
//        Map<Long, Double> studentAverages = new HashMap<>();
//        for (StudentDataService.StudentRecord student : students) {
//            Map<String, Double> grades = studentGrades.get(student.getId());
//            if (grades != null && !grades.isEmpty()) {
//                double average = grades.values().stream()
//                        .mapToDouble(Double::doubleValue)
//                        .average()
//                        .orElse(5.0);
//                studentAverages.put(student.getId(), average);
//            }
//        }
//
//        // Sort optional courses by "popularity" (simulated)
//        List<OptionalCourse> sortedCourses = new ArrayList<>(optionalCourses);
//        Collections.sort(sortedCourses, (c1, c2) -> {
//            // Courses with lower ID are considered more popular in demo
//            return c1.getId().compareTo(c2.getId());
//        });
//
//        for (StudentDataService.StudentRecord student : students) {
//            Double studentAverage = studentAverages.getOrDefault(student.getId(), 5.0);
//
//            // Number of options: students with higher averages choose more options
//            int numOptions;
//            if (studentAverage >= 9.0) numOptions = 5;
//            else if (studentAverage >= 8.0) numOptions = 4;
//            else numOptions = 3;
//
//            // Choose courses: students with high averages have better chances at popular courses
//            List<OptionalCourse> availableCourses = new ArrayList<>(sortedCourses);
//
//            if (studentAverage >= 8.5) {
//                // Good students prioritize the first courses in the list (the popular ones)
//                // Don't shuffle, take the first numOptions
//            } else {
//                // Others shuffle the courses
//                Collections.shuffle(availableCourses, random);
//            }
//
//            // Limit to available number
//            int actualOptions = Math.min(numOptions, availableCourses.size());
//
//            for (int i = 0; i < actualOptions; i++) {
//                PreferenceDTO pref = new PreferenceDTO();
//                pref.setStudentId(student.getId());
//                pref.setCourseId(availableCourses.get(i).getId());
//                pref.setPriority(i + 1);
//                preferences.add(pref);
//            }
//        }
//
//        return preferences;
//    }
//
//    private DemoResultDTO buildDemoResult(
//            List<StudentDataService.StudentRecord> students,
//            List<MandatoryCourse> mandatoryCourses,
//            List<OptionalCourse> optionalCourses,
//            Map<Long, Map<String, Double>> studentGrades,
//            List<PreferenceDTO> preferences,
//            List<StudentAllocationDTO> allocations,
//            long executionTime
//    ) {
//        // Calculate statistics
//        Map<Integer, Long> rankDistribution = allocations.stream()
//                .collect(Collectors.groupingBy(
//                        StudentAllocationDTO::getPreferenceRank,
//                        Collectors.counting()
//                ));
//
//        Map<String, Long> courseStats = allocations.stream()
//                .collect(Collectors.groupingBy(
//                        allocation -> optionalCourses.stream()
//                                .filter(c -> c.getId().equals(allocation.getAllocatedCourseId()))
//                                .findFirst()
//                                .map(CourseBase::getName)
//                                .orElse("Unknown"),
//                        Collectors.counting()
//                ));
//
//        // Create allocation details
//        List<DemoResultDTO.AllocationDetailDTO> allocationDetails = allocations.stream()
//                .limit(50) // Limit to first 50 for performance
//                .map(allocation -> {
//                    // Find the student
//                    StudentDataService.StudentRecord student = students.stream()
//                            .filter(s -> s.getId().equals(allocation.getStudentId()))
//                            .findFirst()
//                            .orElse(null);
//
//                    // Find the course
//                    OptionalCourse course = optionalCourses.stream()
//                            .filter(c -> c.getId().equals(allocation.getAllocatedCourseId()))
//                            .findFirst()
//                            .orElse(null);
//
//                    // Calculate student average
//                    Double average = student != null ?
//                            studentGrades.get(student.getId()).values().stream()
//                                    .mapToDouble(Double::doubleValue)
//                                    .average()
//                                    .orElse(0.0) : 0.0;
//
//                    return DemoResultDTO.AllocationDetailDTO.builder()
//                            .studentId(allocation.getStudentId())
//                            .studentName(student != null ? student.getName() : "N/A")
//                            .courseId(allocation.getAllocatedCourseId())
//                            .courseName(course != null ? course.getName() : "N/A")
//                            .preferenceRank(allocation.getPreferenceRank())
//                            .studentAverage(Math.round(average * 100.0) / 100.0)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        // Create student sample for display
//        List<DemoResultDTO.StudentDemoDTO> studentSamples = students.stream()
//                .limit(10)
//                .map(student -> {
//                    Map<String, Double> grades = studentGrades.getOrDefault(student.getId(), new HashMap<>());
//                    Double average = grades.values().stream()
//                            .mapToDouble(Double::doubleValue)
//                            .average()
//                            .orElse(0.0);
//
//                    return DemoResultDTO.StudentDemoDTO.builder()
//                            .id(student.getId())
//                            .name(student.getName())
//                            .matriculationNumber(student.getMatriculationNumber())
//                            .series(student.getSeries())
//                            .averageGrade(Math.round(average * 100.0) / 100.0)
//                            .mandatoryCourseGrades(grades)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return DemoResultDTO.builder()
//                .scenarioName("Integrated Demo with Real Data - FMSE 2025-2026")
//                .totalStudents(students.size())
//                .totalMandatoryCourses(mandatoryCourses.size())
//                .totalOptionalCourses(optionalCourses.size())
//                .generatedPreferences(preferences.size())
//                .allocatedStudents(allocations.size())
//                .executionTimeMs(executionTime)
//                .preferenceRankDistribution(rankDistribution)
//                .courseAllocationStats(courseStats)
//                .allocationDetails(allocationDetails)
//                .studentSamples(studentSamples)
//                .build();
//    }
//
//    public DemoResultDTO runQuickDemo() {
//        // Quick version with hardcoded data for testing
//        long startTime = System.currentTimeMillis();
//
//        // Create a simple scenario with 5 students and 3 courses
//        List<PreferenceDTO> quickPrefs = Arrays.asList(
//                createPref(1L, 101L, 1),
//                createPref(1L, 102L, 2),
//                createPref(2L, 101L, 1),
//                createPref(2L, 103L, 2),
//                createPref(3L, 102L, 1),
//                createPref(3L, 103L, 2),
//                createPref(4L, 103L, 1),
//                createPref(4L, 101L, 2),
//                createPref(5L, 102L, 1),
//                createPref(5L, 101L, 2)
//        );
//
//        List<StudentAllocationDTO> allocations = allocationService.executeAllocation(
//                quickPrefs,
//                AllocationStrategy.GALE_SHAPLEY
//        );
//
//        long endTime = System.currentTimeMillis();
//
//        // Build a simple result
//        return DemoResultDTO.builder()
//                .scenarioName("Quick Demo (5 students, 3 courses)")
//                .totalStudents(5)
//                .totalMandatoryCourses(0)
//                .totalOptionalCourses(3)
//                .generatedPreferences(quickPrefs.size())
//                .allocatedStudents(allocations.size())
//                .executionTimeMs(endTime - startTime)
//                .preferenceRankDistribution(allocations.stream()
//                        .collect(Collectors.groupingBy(
//                                StudentAllocationDTO::getPreferenceRank,
//                                Collectors.counting()
//                        )))
//                .build();
//    }
//
//    private PreferenceDTO createPref(Long studentId, Long courseId, Integer priority) {
//        PreferenceDTO dto = new PreferenceDTO();
//        dto.setStudentId(studentId);
//        dto.setCourseId(courseId);
//        dto.setPriority(priority);
//        return dto;
//    }

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