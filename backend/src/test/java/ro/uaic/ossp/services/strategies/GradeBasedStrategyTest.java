package ro.uaic.ossp.services.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.services.strategies.implementations.GradeBasedStrategy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradeBasedStrategyTest {

    private GradeBasedStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new GradeBasedStrategy();
    }

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
    void shouldPrioritizeHigherGradeStudent_WhenCompetingForSameCourse() {
        // Given: Two students competing for the same course
        // Note: In real implementation, grade data would come from Student entity/database
        // For now, we'll assume studentId correlates with grade (higher ID = higher grade)
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 101L, 201L), // lower grade student wants course 201
            new PreferenceDTO(1, 102L, 201L)  // higher grade student wants course 201
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(1, result.size()); // Only one spot available
        
        // Higher grade student (102) should get the course
        StudentAllocationDTO allocation = result.get(0);
        assertEquals(102L, allocation.getStudentId());
        assertEquals(201L, allocation.getAllocatedCourseId());
    }

    @Test
    void shouldAllocateAlternativePreference_WhenFirstChoiceUnavailable() {
        // Given: Two students where one gets first choice, other should get alternative
        List<PreferenceDTO> preferences = List.of(
            // Higher grade student - should get first choice
            new PreferenceDTO(1, 102L, 201L),
            new PreferenceDTO(2, 102L, 202L),
            
            // Lower grade student - should get alternative
            new PreferenceDTO(1, 101L, 201L), // loses competition for 201
            new PreferenceDTO(2, 101L, 203L)  // should get 203 instead
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(2, result.size());
        
        // Higher grade student gets first choice
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(102L) && a.getAllocatedCourseId().equals(201L)));
            
        // Lower grade student gets alternative
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(101L) && a.getAllocatedCourseId().equals(203L)));
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
    void shouldRankStudentsByGrade_WhenMultipleCompetitors() {
        // Given: Three students competing for two courses
        List<PreferenceDTO> preferences = List.of(
            // Student 103 (highest grade) - first choice
            new PreferenceDTO(1, 103L, 201L),
            new PreferenceDTO(2, 103L, 202L),
            
            // Student 102 (medium grade) - first choice  
            new PreferenceDTO(1, 102L, 201L),
            new PreferenceDTO(2, 102L, 202L),
            
            // Student 101 (lowest grade) - first choice
            new PreferenceDTO(1, 101L, 201L),
            new PreferenceDTO(2, 101L, 203L)
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(3, result.size());
        
        // Highest grade student should get course 201
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(103L) && a.getAllocatedCourseId().equals(201L)));
            
        // Medium grade student should get course 202 (their second choice)
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(102L) && a.getAllocatedCourseId().equals(202L)));
            
        // Lowest grade student should get course 203 (their alternative)
        assertTrue(result.stream().anyMatch(a -> 
            a.getStudentId().equals(101L) && a.getAllocatedCourseId().equals(203L)));
    }

    @Test
    void shouldHandleStudentWithOnlyOnePreference() {
        // Given: Student with single preference that conflicts with higher-grade student
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 102L, 201L), // higher grade student
            new PreferenceDTO(1, 101L, 201L)  // lower grade student, only preference
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(1, result.size()); // Only higher grade student gets allocated
        
        StudentAllocationDTO allocation = result.get(0);
        assertEquals(102L, allocation.getStudentId());
        assertEquals(201L, allocation.getAllocatedCourseId());
    }

    @Test
    void shouldMaintainPreferenceRankingInResult() {
        // Given: Student gets their second choice due to competition
        List<PreferenceDTO> preferences = List.of(
            new PreferenceDTO(1, 102L, 201L), // higher grade student gets first choice
            
            new PreferenceDTO(1, 101L, 201L), // lower grade student's first choice (blocked)
            new PreferenceDTO(2, 101L, 202L)  // lower grade student's second choice (gets this)
        );

        // When
        List<StudentAllocationDTO> result = strategy.executeAllocation(preferences);

        // Then
        assertEquals(2, result.size());
        
        // Check that preference rank is correctly recorded
        StudentAllocationDTO student101 = result.stream()
            .filter(a -> a.getStudentId().equals(101L))
            .findFirst()
            .orElseThrow();
            
        assertEquals(202L, student101.getAllocatedCourseId());
        assertEquals(2, student101.getPreferenceRank()); // Should reflect it was their 2nd choice
    }
}
