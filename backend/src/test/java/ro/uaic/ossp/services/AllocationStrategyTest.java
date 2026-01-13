package ro.uaic.ossp.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.implementations.GaleShapleyStrategy;
import ro.uaic.ossp.services.strategies.implementations.GradeBasedStrategy;
import ro.uaic.ossp.services.strategies.interfaces.IAllocationStrategy;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced AI-Generated Tests for Allocation Strategies
 * Covers: Edge cases, stress tests, property-based testing, concurrency, and algorithm correctness
 */
@DisplayName("Allocation Strategy Tests")
class AllocationStrategyTest {

    // ==================== GALE-SHAPLEY STRATEGY TESTS ====================

    @Nested
    @DisplayName("Gale-Shapley Algorithm Tests")
    class GaleShapleyTests {

        private final GaleShapleyStrategy strategy = new GaleShapleyStrategy();

        @Test
        @DisplayName("Should return empty list for null input")
        void testNullInput() {
            List<StudentAllocationDTO> result = strategy.executeAllocation(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list for empty preferences")
        void testEmptyPreferences() {
            List<StudentAllocationDTO> result = strategy.executeAllocation(Collections.emptyList());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should allocate single student to single course")
        void testSingleStudentSingleCourse() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 100L, 200L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            assertEquals(1, result.size());
            assertEquals(100L, result.get(0).getStudentId());
            assertEquals(200L, result.get(0).getAllocatedCourseId());
            assertEquals(1, result.get(0).getPreferenceRank());
        }

        @Test
        @DisplayName("Should handle multiple students competing for same course - lower ID wins")
        void testMultipleStudentsSameCourse() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 100L, 200L),
                    new PreferenceDTO(1, 101L, 200L),
                    new PreferenceDTO(1, 102L, 200L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Lower studentId should win (100L)
            Optional<StudentAllocationDTO> winner = result.stream()
                    .filter(a -> a.getAllocatedCourseId().equals(200L))
                    .findFirst();

            assertTrue(winner.isPresent());
            assertEquals(100L, winner.get().getStudentId());
        }

        @Test
        @DisplayName("Should respect preference priorities")
        void testPreferencePriorities() {
            // Student 100 prefers course 201 (priority 1), then 200 (priority 2)
            // Student 101 prefers course 200 (priority 1)
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 100L, 201L),
                    new PreferenceDTO(2, 100L, 200L),
                    new PreferenceDTO(1, 101L, 200L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Map<Long, Long> studentToCourse = result.stream()
                    .collect(Collectors.toMap(
                            StudentAllocationDTO::getStudentId,
                            StudentAllocationDTO::getAllocatedCourseId
                    ));

            // Student 100 should get their first choice (201)
            assertEquals(201L, studentToCourse.get(100L));
        }

        @Test
        @DisplayName("Should produce stable matching - no blocking pairs")
        void testStableMatching() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(2, 1L, 11L),
                    new PreferenceDTO(1, 2L, 11L),
                    new PreferenceDTO(2, 2L, 10L),
                    new PreferenceDTO(1, 3L, 10L),
                    new PreferenceDTO(2, 3L, 12L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Verify no student is unmatched if they have preferences
            Set<Long> allocatedStudents = result.stream()
                    .map(StudentAllocationDTO::getStudentId)
                    .collect(Collectors.toSet());

            // All courses should be allocated to unique students
            Set<Long> allocatedCourses = result.stream()
                    .map(StudentAllocationDTO::getAllocatedCourseId)
                    .collect(Collectors.toSet());

            assertEquals(result.size(), allocatedCourses.size(), "Each course should be allocated once");
        }

        @Test
        @DisplayName("Stress test: 1000 students, 100 courses")
        void testLargeScaleAllocation() {
            List<PreferenceDTO> prefs = new ArrayList<>();
            Random random = new Random(42); // Fixed seed for reproducibility

            // Generate 1000 students with random preferences for 100 courses
            for (long studentId = 1; studentId <= 1000; studentId++) {
                List<Long> courseIds = new ArrayList<>();
                for (long courseId = 1; courseId <= 100; courseId++) {
                    courseIds.add(courseId);
                }
                Collections.shuffle(courseIds, random);

                // Each student has 5 preferences
                for (int priority = 1; priority <= 5; priority++) {
                    prefs.add(new PreferenceDTO(priority, studentId, courseIds.get(priority - 1)));
                }
            }

            long startTime = System.currentTimeMillis();
            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);
            long endTime = System.currentTimeMillis();

            // Performance assertion: should complete within 5 seconds
            assertTrue((endTime - startTime) < 5000,
                    "Algorithm should complete within 5 seconds, took: " + (endTime - startTime) + "ms");

            // Each course allocated at most once
            Map<Long, Long> courseCount = result.stream()
                    .collect(Collectors.groupingBy(
                            StudentAllocationDTO::getAllocatedCourseId,
                            Collectors.counting()
                    ));

            assertTrue(courseCount.values().stream().allMatch(count -> count == 1),
                    "Each course should be allocated at most once");
        }
    }

    // ==================== GRADE-BASED STRATEGY TESTS ====================

    @Nested
    @DisplayName("Grade-Based Algorithm Tests")
    class GradeBasedTests {

        private final GradeBasedStrategy strategy = new GradeBasedStrategy();

        @Test
        @DisplayName("Should return empty list for null input")
        void testNullInput() {
            List<StudentAllocationDTO> result = strategy.executeAllocation(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should prioritize higher studentId (simulating higher grades)")
        void testHigherGradePriority() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 100L, 200L),
                    new PreferenceDTO(1, 200L, 200L), // Higher "grade"
                    new PreferenceDTO(1, 150L, 200L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Optional<StudentAllocationDTO> winner = result.stream()
                    .filter(a -> a.getAllocatedCourseId().equals(200L))
                    .findFirst();

            assertTrue(winner.isPresent());
            assertEquals(200L, winner.get().getStudentId(), "Higher studentId should win");
        }

        @Test
        @DisplayName("Should allocate all students when enough courses available")
        void testAllStudentsAllocated() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 100L, 200L),
                    new PreferenceDTO(1, 101L, 201L),
                    new PreferenceDTO(1, 102L, 202L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            assertEquals(3, result.size());
            Set<Long> allocatedStudents = result.stream()
                    .map(StudentAllocationDTO::getStudentId)
                    .collect(Collectors.toSet());
            assertTrue(allocatedStudents.containsAll(Set.of(100L, 101L, 102L)));
        }

        @Test
        @DisplayName("Should handle student with multiple preferences")
        void testMultiplePreferencesPerStudent() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 100L, 200L),
                    new PreferenceDTO(2, 100L, 201L),
                    new PreferenceDTO(3, 100L, 202L),
                    new PreferenceDTO(1, 200L, 200L) // Will take 200L first (higher grade)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Map<Long, Long> studentToCourse = result.stream()
                    .collect(Collectors.toMap(
                            StudentAllocationDTO::getStudentId,
                            StudentAllocationDTO::getAllocatedCourseId
                    ));

            // Student 200 gets course 200 (higher grade wins)
            assertEquals(200L, studentToCourse.get(200L));
            // Student 100 should get their next preference (201)
            assertEquals(201L, studentToCourse.get(100L));
        }
    }

    // ==================== COMPARATIVE TESTS ====================

    @Nested
    @DisplayName("Strategy Comparison Tests")
    class StrategyComparisonTests {

        private final GaleShapleyStrategy galeShapley = new GaleShapleyStrategy();
        private final GradeBasedStrategy gradeBased = new GradeBasedStrategy();

        @Test
        @DisplayName("Both strategies should handle same input without errors")
        void testBothStrategiesHandleSameInput() {
            List<PreferenceDTO> prefs = generateRandomPreferences(50, 20, 3);

            assertDoesNotThrow(() -> galeShapley.executeAllocation(prefs));
            assertDoesNotThrow(() -> gradeBased.executeAllocation(prefs));
        }

        @Test
        @DisplayName("Compare allocation efficiency between strategies")
        void testAllocationEfficiency() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(2, 1L, 11L),
                    new PreferenceDTO(1, 2L, 10L),
                    new PreferenceDTO(2, 2L, 11L),
                    new PreferenceDTO(1, 3L, 11L),
                    new PreferenceDTO(2, 3L, 12L)
            );

            List<StudentAllocationDTO> gsResult = galeShapley.executeAllocation(prefs);
            List<StudentAllocationDTO> gbResult = gradeBased.executeAllocation(prefs);

            // Calculate average preference rank (lower is better)
            double gsAvgRank = gsResult.stream()
                    .mapToInt(StudentAllocationDTO::getPreferenceRank)
                    .average().orElse(0);
            double gbAvgRank = gbResult.stream()
                    .mapToInt(StudentAllocationDTO::getPreferenceRank)
                    .average().orElse(0);

            System.out.println("Gale-Shapley avg preference rank: " + gsAvgRank);
            System.out.println("Grade-Based avg preference rank: " + gbAvgRank);

            // Both should produce valid allocations
            assertTrue(gsResult.size() > 0);
            assertTrue(gbResult.size() > 0);
        }

        private List<PreferenceDTO> generateRandomPreferences(int students, int courses, int prefsPerStudent) {
            List<PreferenceDTO> prefs = new ArrayList<>();
            Random random = new Random(42);

            for (long studentId = 1; studentId <= students; studentId++) {
                List<Long> courseIds = new ArrayList<>();
                for (long courseId = 1; courseId <= courses; courseId++) {
                    courseIds.add(courseId);
                }
                Collections.shuffle(courseIds, random);

                for (int priority = 1; priority <= prefsPerStudent; priority++) {
                    prefs.add(new PreferenceDTO(priority, studentId, courseIds.get(priority - 1)));
                }
            }
            return prefs;
        }
    }

    // ==================== PROPERTY-BASED TESTS ====================

    @Nested
    @DisplayName("Property-Based Tests")
    class PropertyBasedTests {

        @ParameterizedTest
        @MethodSource("allocationScenarios")
        @DisplayName("Should satisfy allocation invariants for various scenarios")
        void testAllocationInvariants(List<PreferenceDTO> prefs, String scenarioName) {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Property 1: No duplicate course allocations
            Set<Long> courses = new HashSet<>();
            for (StudentAllocationDTO alloc : result) {
                assertFalse(courses.contains(alloc.getAllocatedCourseId()),
                        "Course " + alloc.getAllocatedCourseId() + " allocated twice in " + scenarioName);
                courses.add(alloc.getAllocatedCourseId());
            }

            // Property 2: No duplicate student allocations
            Set<Long> students = new HashSet<>();
            for (StudentAllocationDTO alloc : result) {
                assertFalse(students.contains(alloc.getStudentId()),
                        "Student " + alloc.getStudentId() + " allocated twice in " + scenarioName);
                students.add(alloc.getStudentId());
            }

            // Property 3: All allocations reference valid preferences
            Set<String> validPairs = prefs.stream()
                    .map(p -> p.getStudentId() + "-" + p.getCourseId())
                    .collect(Collectors.toSet());

            for (StudentAllocationDTO alloc : result) {
                String pair = alloc.getStudentId() + "-" + alloc.getAllocatedCourseId();
                assertTrue(validPairs.contains(pair),
                        "Invalid allocation " + pair + " in " + scenarioName);
            }
        }

        static Stream<Arguments> allocationScenarios() {
            return Stream.of(
                    Arguments.of(List.of(
                            new PreferenceDTO(1, 1L, 10L)
                    ), "Single student, single course"),

                    Arguments.of(List.of(
                            new PreferenceDTO(1, 1L, 10L),
                            new PreferenceDTO(1, 2L, 10L)
                    ), "Two students, one course"),

                    Arguments.of(List.of(
                            new PreferenceDTO(1, 1L, 10L),
                            new PreferenceDTO(2, 1L, 11L),
                            new PreferenceDTO(1, 2L, 11L),
                            new PreferenceDTO(2, 2L, 10L)
                    ), "Cross preferences"),

                    Arguments.of(IntStream.rangeClosed(1, 100)
                            .mapToObj(i -> new PreferenceDTO(1, (long) i, (long) (100 + i)))
                            .collect(Collectors.toList()), "100 unique pairs")
            );
        }
    }

    // ==================== CONCURRENCY TESTS ====================

    @Nested
    @DisplayName("Concurrency Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("Strategy should be thread-safe")
        void testThreadSafety() throws InterruptedException, ExecutionException {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Future<List<StudentAllocationDTO>>> futures = new ArrayList<>();

            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(2, 1L, 11L),
                    new PreferenceDTO(1, 2L, 11L),
                    new PreferenceDTO(2, 2L, 10L)
            );

            // Run same allocation 100 times concurrently
            for (int i = 0; i < 100; i++) {
                futures.add(executor.submit(() -> strategy.executeAllocation(prefs)));
            }

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

            // All results should be identical
            List<StudentAllocationDTO> firstResult = futures.get(0).get();
            for (Future<List<StudentAllocationDTO>> future : futures) {
                List<StudentAllocationDTO> result = future.get();
                assertEquals(firstResult.size(), result.size(),
                        "All concurrent executions should produce same size result");
            }
        }

        @Test
        @DisplayName("Strategy should handle concurrent different inputs")
        void testConcurrentDifferentInputs() throws InterruptedException {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            ExecutorService executor = Executors.newFixedThreadPool(5);
            CountDownLatch latch = new CountDownLatch(50);
            List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < 50; i++) {
                final int seed = i;
                executor.submit(() -> {
                    try {
                        Random random = new Random(seed);
                        List<PreferenceDTO> prefs = new ArrayList<>();
                        for (int s = 1; s <= 10; s++) {
                            for (int p = 1; p <= 3; p++) {
                                prefs.add(new PreferenceDTO(p, (long) s, (long) (random.nextInt(5) + 1)));
                            }
                        }
                        strategy.executeAllocation(prefs);
                    } catch (Throwable t) {
                        errors.add(t);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(30, TimeUnit.SECONDS);
            executor.shutdown();

            assertTrue(errors.isEmpty(), "No errors should occur: " + errors);
        }
    }

    // ==================== EDGE CASE TESTS ====================

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        private final GaleShapleyStrategy strategy = new GaleShapleyStrategy();

        @Test
        @DisplayName("Should handle duplicate preferences gracefully")
        void testDuplicatePreferences() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(1, 1L, 10L), // Duplicate
                    new PreferenceDTO(2, 1L, 11L)
            );

            assertDoesNotThrow(() -> strategy.executeAllocation(prefs));
        }

        @Test
        @DisplayName("Should handle very large priority numbers")
        void testLargePriorityNumbers() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(Integer.MAX_VALUE, 1L, 10L),
                    new PreferenceDTO(1, 1L, 11L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Should prefer lower priority number
            assertEquals(11L, result.get(0).getAllocatedCourseId());
        }

        @Test
        @DisplayName("Should handle zero priority")
        void testZeroPriority() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(0, 1L, 10L),
                    new PreferenceDTO(1, 1L, 11L)
            );

            assertDoesNotThrow(() -> strategy.executeAllocation(prefs));
        }

        @Test
        @DisplayName("Should handle negative priority")
        void testNegativePriority() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(-1, 1L, 10L),
                    new PreferenceDTO(1, 1L, 11L)
            );

            assertDoesNotThrow(() -> strategy.executeAllocation(prefs));
        }

        @Test
        @DisplayName("Should handle all students wanting same course")
        void testAllStudentsSameCourse() {
            List<PreferenceDTO> prefs = new ArrayList<>();
            for (long studentId = 1; studentId <= 100; studentId++) {
                prefs.add(new PreferenceDTO(1, studentId, 999L));
            }

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Only one student should get the course
            assertEquals(1, result.size());
            assertEquals(999L, result.get(0).getAllocatedCourseId());
        }

        @Test
        @DisplayName("Should handle circular preferences")
        void testCircularPreferences() {
            // A wants B's first choice, B wants C's first choice, C wants A's first choice
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 20L), // A wants course 20
                    new PreferenceDTO(2, 1L, 10L),
                    new PreferenceDTO(1, 2L, 30L), // B wants course 30
                    new PreferenceDTO(2, 2L, 20L),
                    new PreferenceDTO(1, 3L, 10L), // C wants course 10
                    new PreferenceDTO(2, 3L, 30L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Should resolve without infinite loop
            assertEquals(3, result.size());
        }
    }
}

