package ro.uaic.ossp.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.implementations.GaleShapleyStrategy;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GaleShapleyStrategyUnitTest {

    private GaleShapleyStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new GaleShapleyStrategy();
    }

    // ===== TEST 1: Empty input =====
    @Test
    void testExecuteAllocation_WithNullInput_ReturnsEmptyList() {
        List<StudentAllocationDTO> result = strategy.executeAllocation(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExecuteAllocation_WithEmptyList_ReturnsEmptyList() {
        List<PreferenceDTO> emptyList = new ArrayList<>();
        List<StudentAllocationDTO> result = strategy.executeAllocation(emptyList);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===== TEST 2: Single student, single course =====
    @Test
    void testExecuteAllocation_SingleStudentSingleCourse_AllocatesCorrectly() {
        List<PreferenceDTO> preferences = new ArrayList<>();
        PreferenceDTO pref = createPreference(1L, 101L, 1);
        preferences.add(pref);

        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStudentId());
        assertEquals(101L, result.get(0).getAllocatedCourseId());
        assertEquals(1, result.get(0).getPreferenceRank());
    }

    // ===== TEST 3: Multiple students, one course =====
    @Test
    void testExecuteAllocation_TwoStudentsOneCourse_LowerIdWins() {
        List<PreferenceDTO> preferences = new ArrayList<>();
        // Student 1 wants course 101
        preferences.add(createPreference(1L, 101L, 1));
        // Student 2 also wants course 101
        preferences.add(createPreference(2L, 101L, 1));

        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Only one student can get the course (student 1, because lower ID)
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStudentId());
        assertEquals(101L, result.get(0).getAllocatedCourseId());
    }

    // ===== TEST 4: Full Scenario =====
    @Test
    void testExecuteAllocation_MultipleStudentsMultipleCourses_StableMatching() {
        List<PreferenceDTO> preferences = new ArrayList<>();

        // Student 1: Course A (1st), Course B (2nd)
        preferences.add(createPreference(1L, 101L, 1));
        preferences.add(createPreference(1L, 102L, 2));

        // Student 2: Course B (1st), Course A (2nd)
        preferences.add(createPreference(2L, 102L, 1));
        preferences.add(createPreference(2L, 101L, 2));

        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Should allocate both students
        assertEquals(2, result.size());

        // Verify stable matching: Student 1 gets Course A, Student 2 gets Course B
        boolean student1GotA = false;
        boolean student2GotB = false;

        for (StudentAllocationDTO allocation : result) {
            if (allocation.getStudentId() == 1L && allocation.getAllocatedCourseId() == 101L) {
                student1GotA = true;
            }
            if (allocation.getStudentId() == 2L && allocation.getAllocatedCourseId() == 102L) {
                student2GotB = true;
            }
        }

        assertTrue(student1GotA, "Student 1 should get Course A");
        assertTrue(student2GotB, "Student 2 should get Course B");
    }

    // ===== TEST 5: Preference ranking in result =====
    @Test
    void testExecuteAllocation_ReturnsCorrectPreferenceRank() {
        List<PreferenceDTO> preferences = new ArrayList<>();

        // Student ranks 3 courses
        preferences.add(createPreference(1L, 101L, 1));
        preferences.add(createPreference(1L, 102L, 2));
        preferences.add(createPreference(1L, 103L, 3));

        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Student should get first choice
        assertEquals(1, result.get(0).getPreferenceRank());
    }

    // ===== TEST 6: More students than courses =====
    @Test
    void testExecuteAllocation_MoreStudentsThanCourses_OnlyCourseCountAllocated() {
        List<PreferenceDTO> preferences = new ArrayList<>();

        // 5 students all want the same 2 courses
        for (long studentId = 1; studentId <= 5; studentId++) {
            preferences.add(createPreference(studentId, 101L, 1));
            preferences.add(createPreference(studentId, 102L, 2));
        }

        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Only 2 courses available, so only 2 allocations
        assertEquals(2, result.size());

        boolean hasStudent1 = false;
        boolean hasStudent2 = false;

        for (StudentAllocationDTO allocation : result) {
            if (allocation.getStudentId() == 1L) hasStudent1 = true;
            if (allocation.getStudentId() == 2L) hasStudent2 = true;
        }

        assertTrue(hasStudent1, "Student 1 (lowest ID) should get a course");
        assertTrue(hasStudent2, "Student 2 (second lowest ID) should get a course");
    }

    private PreferenceDTO createPreference(Long studentId, Long courseId, Integer priority) {
        PreferenceDTO dto = new PreferenceDTO();
        dto.setStudentId(studentId);
        dto.setCourseId(courseId);
        dto.setPriority(priority);
        return dto;
    }
}