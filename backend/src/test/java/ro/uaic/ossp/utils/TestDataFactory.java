package ro.uaic.ossp.utils;

import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.models.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * AI-Generated Test Utilities
 * Provides factory methods and builders for test data generation
 */
public class TestDataFactory {

    private static final Random RANDOM = new Random(42);

    // ==================== PREFERENCE GENERATORS ====================

    /**
     * Generate random preferences for testing allocation algorithms
     */
    public static List<PreferenceDTO> generateRandomPreferences(int numStudents, int numCourses, int prefsPerStudent) {
        List<PreferenceDTO> preferences = new ArrayList<>();

        for (long studentId = 1; studentId <= numStudents; studentId++) {
            List<Long> shuffledCourses = IntStream.rangeClosed(1, numCourses)
                    .mapToObj(Long::valueOf)
                    .collect(Collectors.toList());
            Collections.shuffle(shuffledCourses, RANDOM);

            for (int priority = 1; priority <= Math.min(prefsPerStudent, numCourses); priority++) {
                preferences.add(new PreferenceDTO(priority, studentId, shuffledCourses.get(priority - 1)));
            }
        }

        return preferences;
    }

    /**
     * Generate preferences where all students want the same course (max contention)
     */
    public static List<PreferenceDTO> generateMaxContentionPreferences(int numStudents, long courseId) {
        return IntStream.rangeClosed(1, numStudents)
                .mapToObj(i -> new PreferenceDTO(1, (long) i, courseId))
                .collect(Collectors.toList());
    }

    /**
     * Generate preferences with no overlap (each student wants unique course)
     */
    public static List<PreferenceDTO> generateNoOverlapPreferences(int numStudents) {
        return IntStream.rangeClosed(1, numStudents)
                .mapToObj(i -> new PreferenceDTO(1, (long) i, (long) (i + 100)))
                .collect(Collectors.toList());
    }

    /**
     * Generate circular preference pattern for testing stability
     */
    public static List<PreferenceDTO> generateCircularPreferences(int numStudents) {
        List<PreferenceDTO> prefs = new ArrayList<>();
        for (int i = 1; i <= numStudents; i++) {
            // Each student's first choice is the next student's course
            prefs.add(new PreferenceDTO(1, (long) i, (long) ((i % numStudents) + 1)));
            prefs.add(new PreferenceDTO(2, (long) i, (long) i));
        }
        return prefs;
    }

    // ==================== STUDENT GENERATORS ====================

    /**
     * Create a test student with random data
     */
    public static Student createRandomStudent() {
        return Student.builder()
                .matriculationNumber("MAT" + RANDOM.nextInt(1000000))
                .academicYear(RANDOM.nextInt(4) + 1)
                .specialization(randomSpecialization())
                .groupNumber(randomGroupNumber())
                .build();
    }

    /**
     * Create a batch of test students
     */
    public static List<Student> createStudentBatch(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> Student.builder()
                        .matriculationNumber("BATCH" + String.format("%06d", i))
                        .academicYear((i % 4) + 1)
                        .specialization(randomSpecialization())
                        .groupNumber(randomGroupNumber())
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== COURSE GENERATORS ====================

    /**
     * Create a test optional course
     */
    public static OptionalCourse createRandomCourse(CoursePackage coursePackage) {
        return OptionalCourse.builder()
                .code("OPT" + RANDOM.nextInt(10000))
                .maxStudents(20 + RANDOM.nextInt(30))
                .coursePackage(coursePackage)
                .build();
    }

    /**
     * Create a batch of test courses
     */
    public static List<OptionalCourse> createCourseBatch(int count, CoursePackage coursePackage) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> OptionalCourse.builder()
                        .code("BATCH_C" + String.format("%04d", i))
                        .maxStudents(25)
                        .coursePackage(coursePackage)
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== ENROLLMENT GENERATORS ====================

    /**
     * Create enrollment for student in course
     */
    public static Enrollment createEnrollment(Student student, OptionalCourse course) {
        return Enrollment.builder()
                .student(student)
                .optionalCourse(course)
                .build();
    }

    // ==================== PREFERENCE ENTITY GENERATORS ====================

    /**
     * Create preference entity
     */
    public static Preference createPreference(Student student, OptionalCourse course, int priority) {
        return Preference.builder()
                .priority(priority)
                .student(student)
                .optionalCourse(course)
                .build();
    }

    // ==================== TRANSFER REQUEST GENERATORS ====================

    /**
     * Create a pending transfer request
     */
    public static TransferRequest createPendingTransferRequest(
            Student student, OptionalCourse from, OptionalCourse to) {
        return TransferRequest.builder()
                .student(student)
                .currentCourse(from)
                .requestedCourse(to)
                .requestDate(LocalDate.now())
                .status(ro.uaic.ossp.models.enums.TransferStatus.PENDING)
                .build();
    }

    // ==================== ALLOCATION RESULT GENERATORS ====================

    /**
     * Create expected allocation result for testing
     */
    public static StudentAllocationDTO createExpectedAllocation(
            Long studentId, Long courseId, int rank) {
        return StudentAllocationDTO.builder()
                .studentId(studentId)
                .allocatedCourseId(courseId)
                .preferenceRank(rank)
                .build();
    }

    // ==================== HELPER METHODS ====================

    private static String randomSpecialization() {
        String[] specs = {"Computer Science", "Software Engineering", "Information Technology",
                "Data Science", "Artificial Intelligence", "Cybersecurity"};
        return specs[RANDOM.nextInt(specs.length)];
    }

    private static String randomGroupNumber() {
        char letter = (char) ('A' + RANDOM.nextInt(6));
        int number = RANDOM.nextInt(5) + 1;
        return "" + letter + number;
    }

    // ==================== ASSERTION HELPERS ====================

    /**
     * Verify allocation invariants
     */
    public static void assertValidAllocation(List<StudentAllocationDTO> allocations,
                                              List<PreferenceDTO> preferences) {
        // No duplicate students
        Set<Long> students = new HashSet<>();
        for (StudentAllocationDTO a : allocations) {
            if (students.contains(a.getStudentId())) {
                throw new AssertionError("Duplicate student allocation: " + a.getStudentId());
            }
            students.add(a.getStudentId());
        }

        // No duplicate courses
        Set<Long> courses = new HashSet<>();
        for (StudentAllocationDTO a : allocations) {
            if (courses.contains(a.getAllocatedCourseId())) {
                throw new AssertionError("Duplicate course allocation: " + a.getAllocatedCourseId());
            }
            courses.add(a.getAllocatedCourseId());
        }

        // All allocations based on valid preferences
        Set<String> validPairs = preferences.stream()
                .map(p -> p.getStudentId() + "-" + p.getCourseId())
                .collect(Collectors.toSet());

        for (StudentAllocationDTO a : allocations) {
            String pair = a.getStudentId() + "-" + a.getAllocatedCourseId();
            if (!validPairs.contains(pair)) {
                throw new AssertionError("Invalid allocation: " + pair);
            }
        }
    }

    /**
     * Calculate allocation quality metrics
     */
    public static AllocationMetrics calculateMetrics(List<StudentAllocationDTO> allocations) {
        if (allocations.isEmpty()) {
            return new AllocationMetrics(0, 0, 0, 0);
        }

        double avgRank = allocations.stream()
                .mapToInt(StudentAllocationDTO::getPreferenceRank)
                .average()
                .orElse(0);

        int minRank = allocations.stream()
                .mapToInt(StudentAllocationDTO::getPreferenceRank)
                .min()
                .orElse(0);

        int maxRank = allocations.stream()
                .mapToInt(StudentAllocationDTO::getPreferenceRank)
                .max()
                .orElse(0);

        long firstChoiceCount = allocations.stream()
                .filter(a -> a.getPreferenceRank() == 1)
                .count();

        return new AllocationMetrics(avgRank, minRank, maxRank, (int) firstChoiceCount);
    }

    /**
     * Metrics container for allocation quality
     */
    public record AllocationMetrics(
            double averageRank,
            int minRank,
            int maxRank,
            int firstChoiceAllocations
    ) {
        @Override
        public String toString() {
            return String.format(
                    "AllocationMetrics[avgRank=%.2f, minRank=%d, maxRank=%d, firstChoice=%d]",
                    averageRank, minRank, maxRank, firstChoiceAllocations);
        }
    }
}

