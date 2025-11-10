package ro.uaic.ossp.services.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.implementations.GaleShapleyStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GaleShapleyStrategyTest {
    @Autowired
    private GaleShapleyStrategy strategy;

    @Test
    void shouldAllocateStudentToFirstPreference_WhenNoCompetition() {
        // Given: One student with one preference
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 101L, 201L) // priority 1, student 101, course 201
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(1, result.size());
        
        StudentAllocationDTO allocation = result.get(0);
        assertEquals(101L, allocation.getStudentId());
        assertEquals(201L, allocation.getAllocatedCourseId());
        assertEquals(1, allocation.getPreferenceRank());
    }

    @Test
    void shouldAllocateMultipleStudents_WhenNoCourseConflicts() {
        // Given: Two students with different course preferences
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 101L, 201L), // student 101 wants course 201
            new PreferenceDTO(1, 102L, 202L)  // student 102 wants course 202
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(2, result.size());
        
        // Both students should get their first choice
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(101L) && a.getAllocatedCourseId().equals(201L)));
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(102L) && a.getAllocatedCourseId().equals(202L)));
    }

    @Test
    void shouldHandleCourseCompetition_WithStableMatching() {
        // Given: Two students competing for the same course
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 101L, 201L), // student 101 wants course 201 (priority 1)
            new PreferenceDTO(2, 101L, 202L), // student 101 wants course 202 (priority 2)
            new PreferenceDTO(1, 102L, 201L), // student 102 wants course 201 (priority 1)
            new PreferenceDTO(2, 102L, 203L)  // student 102 wants course 203 (priority 2)
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(2, result.size());
        
        // Should create a stable matching (exact allocation depends on Gale-Shapley implementation)
        // At minimum, both students should be allocated somewhere
        assertTrue(result.stream().anyMatch(a -> a.getStudentId().equals(101L)));
        assertTrue(result.stream().anyMatch(a -> a.getStudentId().equals(102L)));
    }

    @Test
    void shouldHandleEmptyPreferences() {
        // Given: No preferences
        List<PreferenceDTO> preferences = List.of();

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldAllocateBasedOnPreferencePriority() {
        // Given: One student with multiple preferences
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 101L, 201L), // first choice
            new PreferenceDTO(2, 101L, 202L), // second choice
            new PreferenceDTO(3, 101L, 203L)  // third choice
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(1, result.size());
        
        StudentAllocationDTO allocation = result.get(0);
        assertEquals(101L, allocation.getStudentId());
        assertEquals(201L, allocation.getAllocatedCourseId()); // Should get first choice
        assertEquals(1, allocation.getPreferenceRank()); // Priority 1
    }

    @Test
    void shouldHandleMultipleStudentsWithMultiplePreferences() {
        // Given: Complex scenario with multiple students and preferences
        List<PreferenceDTO> preferences = List.of(
            // Student 101 preferences
            new PreferenceDTO(1, 101L, 201L),
            new PreferenceDTO(2, 101L, 202L),
            
            // Student 102 preferences  
            new PreferenceDTO(1, 102L, 202L),
            new PreferenceDTO(2, 102L, 201L),
            
            // Student 103 preferences
            new PreferenceDTO(1, 103L, 203L)
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(3, result.size());
        
        // All students should be allocated
        assertTrue(result.stream().anyMatch(a -> a.getStudentId().equals(101L)));
        assertTrue(result.stream().anyMatch(a -> a.getStudentId().equals(102L)));
        assertTrue(result.stream().anyMatch(a -> a.getStudentId().equals(103L)));
        
        // Student 103 should definitely get course 203 (no competition)
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(103L) && a.getAllocatedCourseId().equals(203L)));
    }
}
