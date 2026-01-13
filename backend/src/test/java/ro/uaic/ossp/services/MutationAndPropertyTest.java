package ro.uaic.ossp.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.implementations.GaleShapleyStrategy;
import ro.uaic.ossp.services.strategies.implementations.GradeBasedStrategy;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Advanced AI-Generated Mutation & Property-Based Tests
 * Covers: Mutation testing scenarios, invariant checking, boundary testing
 */
@DisplayName("Mutation & Property-Based Tests")
class MutationAndPropertyTest {

    // ==================== MUTATION KILLING TESTS ====================
    // These tests are specifically designed to kill common mutations

    @Nested
    @DisplayName("Mutation Killing Tests - Boundary Conditions")
    class BoundaryMutationTests {

        private final GaleShapleyStrategy strategy = new GaleShapleyStrategy();

        @Test
        @DisplayName("Kill mutation: off-by-one in loop bounds")
        void testOffByOneMutation() {
            // Single element should still be processed
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            assertEquals(1, result.size(), "Single preference must produce exactly one allocation");
        }

        @Test
        @DisplayName("Kill mutation: <= vs < comparison")
        void testComparisonMutation() {
            // Two students with same priority for same course
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(1, 2L, 10L)
            );

            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            // Exactly one should win
            assertEquals(1, result.size());
            // Lower ID (1L) should win due to tie-breaking
            assertEquals(1L, result.get(0).getStudentId());
        }

        @Test
        @DisplayName("Kill mutation: != vs == null check")
        void testNullCheckMutation() {
            assertDoesNotThrow(() -> strategy.executeAllocation(null));
            assertTrue(strategy.executeAllocation(null).isEmpty());
        }

        @Test
        @DisplayName("Kill mutation: return empty vs return null")
        void testReturnEmptyMutation() {
            List<StudentAllocationDTO> result = strategy.executeAllocation(Collections.emptyList());

            assertNotNull(result, "Should return empty list, not null");
            assertTrue(result.isEmpty());
        }

        @ParameterizedTest
        @CsvSource({
                "1, 1, 10",
                "1, 2, 10",
                "2, 1, 10",
                "100, 100, 100"
        })
        @DisplayName("Kill mutation: parameter order in constructor")
        void testParameterOrderMutation(int priority, long studentId, long courseId) {
            PreferenceDTO pref = new PreferenceDTO(priority, studentId, courseId);

            assertEquals(priority, pref.getPriority());
            assertEquals(studentId, pref.getStudentId());
            assertEquals(courseId, pref.getCourseId());
        }
    }

    // ==================== INVARIANT TESTS ====================

    @Nested
    @DisplayName("Invariant Tests")
    class InvariantTests {

        @ParameterizedTest
        @MethodSource("generateRandomScenarios")
        @DisplayName("Invariant: Each student appears at most once in allocation")
        void testStudentUniquenessInvariant(List<PreferenceDTO> prefs) {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Set<Long> students = new HashSet<>();
            for (StudentAllocationDTO alloc : result) {
                assertFalse(students.contains(alloc.getStudentId()),
                        "Student " + alloc.getStudentId() + " appears more than once");
                students.add(alloc.getStudentId());
            }
        }

        @ParameterizedTest
        @MethodSource("generateRandomScenarios")
        @DisplayName("Invariant: Each course appears at most once in allocation")
        void testCourseUniquenessInvariant(List<PreferenceDTO> prefs) {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Set<Long> courses = new HashSet<>();
            for (StudentAllocationDTO alloc : result) {
                assertFalse(courses.contains(alloc.getAllocatedCourseId()),
                        "Course " + alloc.getAllocatedCourseId() + " appears more than once");
                courses.add(alloc.getAllocatedCourseId());
            }
        }

        @ParameterizedTest
        @MethodSource("generateRandomScenarios")
        @DisplayName("Invariant: All allocations reference existing preferences")
        void testValidAllocationInvariant(List<PreferenceDTO> prefs) {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Set<String> validPairs = prefs.stream()
                    .map(p -> p.getStudentId() + "-" + p.getCourseId())
                    .collect(Collectors.toSet());

            for (StudentAllocationDTO alloc : result) {
                String pair = alloc.getStudentId() + "-" + alloc.getAllocatedCourseId();
                assertTrue(validPairs.contains(pair),
                        "Allocation " + pair + " is not based on a valid preference");
            }
        }

        @ParameterizedTest
        @MethodSource("generateRandomScenarios")
        @DisplayName("Invariant: Preference rank in result matches input priority")
        void testPreferenceRankInvariant(List<PreferenceDTO> prefs) {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();
            List<StudentAllocationDTO> result = strategy.executeAllocation(prefs);

            Map<String, Integer> prefPriorities = prefs.stream()
                    .collect(Collectors.toMap(
                            p -> p.getStudentId() + "-" + p.getCourseId(),
                            PreferenceDTO::getPriority,
                            (a, b) -> a // Handle duplicates
                    ));

            for (StudentAllocationDTO alloc : result) {
                String pair = alloc.getStudentId() + "-" + alloc.getAllocatedCourseId();
                assertEquals(prefPriorities.get(pair), alloc.getPreferenceRank(),
                        "Preference rank mismatch for " + pair);
            }
        }

        static Stream<List<PreferenceDTO>> generateRandomScenarios() {
            return Stream.of(
                    // Empty
                    Collections.emptyList(),

                    // Single
                    List.of(new PreferenceDTO(1, 1L, 10L)),

                    // Multiple students, single course
                    List.of(
                            new PreferenceDTO(1, 1L, 10L),
                            new PreferenceDTO(1, 2L, 10L),
                            new PreferenceDTO(1, 3L, 10L)
                    ),

                    // Single student, multiple courses
                    List.of(
                            new PreferenceDTO(1, 1L, 10L),
                            new PreferenceDTO(2, 1L, 11L),
                            new PreferenceDTO(3, 1L, 12L)
                    ),

                    // Complex scenario
                    generateComplexScenario(10, 5, 3)
            );
        }

        private static List<PreferenceDTO> generateComplexScenario(int students, int courses, int prefsPerStudent) {
            List<PreferenceDTO> prefs = new ArrayList<>();
            Random random = new Random(42);

            for (long studentId = 1; studentId <= students; studentId++) {
                List<Long> courseIds = new ArrayList<>();
                for (long courseId = 1; courseId <= courses; courseId++) {
                    courseIds.add(courseId);
                }
                Collections.shuffle(courseIds, random);

                for (int priority = 1; priority <= Math.min(prefsPerStudent, courses); priority++) {
                    prefs.add(new PreferenceDTO(priority, studentId, courseIds.get(priority - 1)));
                }
            }
            return prefs;
        }
    }

    // ==================== METAMORPHIC TESTING ====================

    @Nested
    @DisplayName("Metamorphic Tests")
    class MetamorphicTests {

        private final GaleShapleyStrategy strategy = new GaleShapleyStrategy();

        @Test
        @DisplayName("Metamorphic: Adding unrelated student shouldn't change existing allocations")
        void testAddingUnrelatedStudent() {
            List<PreferenceDTO> originalPrefs = new ArrayList<>(List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(1, 2L, 11L)
            ));

            List<StudentAllocationDTO> originalResult = strategy.executeAllocation(originalPrefs);
            Map<Long, Long> originalAllocs = originalResult.stream()
                    .collect(Collectors.toMap(
                            StudentAllocationDTO::getStudentId,
                            StudentAllocationDTO::getAllocatedCourseId
                    ));

            // Add unrelated student (different course)
            List<PreferenceDTO> modifiedPrefs = new ArrayList<>(originalPrefs);
            modifiedPrefs.add(new PreferenceDTO(1, 3L, 12L)); // Course 12 not in original

            List<StudentAllocationDTO> modifiedResult = strategy.executeAllocation(modifiedPrefs);
            Map<Long, Long> modifiedAllocs = modifiedResult.stream()
                    .collect(Collectors.toMap(
                            StudentAllocationDTO::getStudentId,
                            StudentAllocationDTO::getAllocatedCourseId
                    ));

            // Original allocations should be preserved
            for (Long studentId : originalAllocs.keySet()) {
                assertEquals(originalAllocs.get(studentId), modifiedAllocs.get(studentId),
                        "Allocation for student " + studentId + " changed unexpectedly");
            }
        }

        @Test
        @DisplayName("Metamorphic: Shuffling input order shouldn't change result (for deterministic algorithm)")
        void testInputOrderIndependence() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(2, 1L, 11L),
                    new PreferenceDTO(1, 2L, 11L),
                    new PreferenceDTO(2, 2L, 10L)
            );

            Set<String> originalAllocSet = strategy.executeAllocation(prefs).stream()
                    .map(a -> a.getStudentId() + "-" + a.getAllocatedCourseId())
                    .collect(Collectors.toSet());

            // Shuffle and run again
            List<PreferenceDTO> shuffled = new ArrayList<>(prefs);
            Collections.shuffle(shuffled, new Random(123));

            Set<String> shuffledAllocSet = strategy.executeAllocation(shuffled).stream()
                    .map(a -> a.getStudentId() + "-" + a.getAllocatedCourseId())
                    .collect(Collectors.toSet());

            assertEquals(originalAllocSet, shuffledAllocSet,
                    "Allocation should not depend on input order");
        }

        @Test
        @DisplayName("Metamorphic: Removing allocated student shouldn't affect other allocations")
        void testRemovingStudent() {
            List<PreferenceDTO> prefs = new ArrayList<>(List.of(
                    new PreferenceDTO(1, 1L, 10L),
                    new PreferenceDTO(1, 2L, 11L),
                    new PreferenceDTO(1, 3L, 12L)
            ));

            List<StudentAllocationDTO> fullResult = strategy.executeAllocation(prefs);

            // Remove student 2
            List<PreferenceDTO> reducedPrefs = prefs.stream()
                    .filter(p -> p.getStudentId() != 2L)
                    .collect(Collectors.toList());

            List<StudentAllocationDTO> reducedResult = strategy.executeAllocation(reducedPrefs);

            // Students 1 and 3 should have same allocations
            Map<Long, Long> fullAllocs = fullResult.stream()
                    .collect(Collectors.toMap(
                            StudentAllocationDTO::getStudentId,
                            StudentAllocationDTO::getAllocatedCourseId
                    ));
            Map<Long, Long> reducedAllocs = reducedResult.stream()
                    .collect(Collectors.toMap(
                            StudentAllocationDTO::getStudentId,
                            StudentAllocationDTO::getAllocatedCourseId
                    ));

            assertEquals(fullAllocs.get(1L), reducedAllocs.get(1L));
            assertEquals(fullAllocs.get(3L), reducedAllocs.get(3L));
        }
    }

    // ==================== PERFORMANCE & TIMEOUT TESTS ====================

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should complete allocation within timeout")
        void testAllocationTimeout() {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();

            List<PreferenceDTO> largeInput = IntStream.rangeClosed(1, 500)
                    .boxed()
                    .flatMap(studentId -> IntStream.rangeClosed(1, 10)
                            .mapToObj(priority -> new PreferenceDTO(
                                    priority,
                                    (long) studentId,
                                    (long) (priority + (studentId % 50))
                            )))
                    .collect(Collectors.toList());

            assertTimeout(Duration.ofSeconds(5), () -> {
                strategy.executeAllocation(largeInput);
            }, "Allocation should complete within 5 seconds");
        }

        @Test
        @DisplayName("Memory efficiency: should not create excessive objects")
        void testMemoryEfficiency() {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();

            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

            List<PreferenceDTO> prefs = IntStream.rangeClosed(1, 1000)
                    .mapToObj(i -> new PreferenceDTO(1, (long) i, (long) (i % 100 + 1)))
                    .collect(Collectors.toList());

            strategy.executeAllocation(prefs);

            runtime.gc();
            long afterMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = afterMemory - beforeMemory;

            // Should use less than 50MB for this operation
            assertTrue(memoryUsed < 50 * 1024 * 1024,
                    "Memory usage too high: " + (memoryUsed / 1024 / 1024) + "MB");
        }

        @Test
        @DisplayName("Scalability: O(n²) or better complexity")
        void testScalabilityComplexity() {
            GaleShapleyStrategy strategy = new GaleShapleyStrategy();

            // Measure time for n=100
            List<PreferenceDTO> small = generatePrefs(100, 10);
            long startSmall = System.nanoTime();
            strategy.executeAllocation(small);
            long timeSmall = System.nanoTime() - startSmall;

            // Measure time for n=400 (4x larger)
            List<PreferenceDTO> large = generatePrefs(400, 10);
            long startLarge = System.nanoTime();
            strategy.executeAllocation(large);
            long timeLarge = System.nanoTime() - startLarge;

            // For O(n²), time should increase by ~16x; for O(n log n), ~4-5x
            // Allow up to 25x to account for overhead
            double ratio = (double) timeLarge / timeSmall;
            assertTrue(ratio < 25,
                    "Algorithm complexity seems worse than O(n²). Ratio: " + ratio);
        }

        private List<PreferenceDTO> generatePrefs(int students, int prefsPerStudent) {
            return IntStream.rangeClosed(1, students)
                    .boxed()
                    .flatMap(s -> IntStream.rangeClosed(1, prefsPerStudent)
                            .mapToObj(p -> new PreferenceDTO(p, (long) s, (long) (p + s % 50))))
                    .collect(Collectors.toList());
        }
    }
}

