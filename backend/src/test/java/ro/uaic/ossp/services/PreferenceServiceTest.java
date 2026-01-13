package ro.uaic.ossp.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.uaic.ossp.dtos.PreferenceDTO;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.Preference;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.OptionalCourseRepository;
import ro.uaic.ossp.repositories.PreferenceRepository;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Advanced AI-Generated Tests for PreferenceService
 * Covers: Unit tests, mocking, edge cases, validation, and behavioral testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PreferenceService Tests")
class PreferenceServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private OptionalCourseRepository optionalCourseRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private PreferenceService preferenceService;

    private Student testStudent;
    private OptionalCourse testCourse1;
    private OptionalCourse testCourse2;

    @BeforeEach
    void setUp() {
        testStudent = Student.builder()
                .matriculationNumber("123456")
                .academicYear(2)
                .specialization("Computer Science")
                .groupNumber("A1")
                .build();
        // Set ID via reflection since it's generated
        setId(testStudent, 1L);

        testCourse1 = OptionalCourse.builder()
                .code("OPT001")
                .maxStudents(30)
                .build();
        setIdForCourse(testCourse1, 100L);

        testCourse2 = OptionalCourse.builder()
                .code("OPT002")
                .maxStudents(25)
                .build();
        setIdForCourse(testCourse2, 101L);
    }

    // ==================== SAVE PREFERENCES TESTS ====================

    @Nested
    @DisplayName("savePreferences() Tests")
    class SavePreferencesTests {

        @Test
        @DisplayName("Should throw exception when preference list is empty")
        void testEmptyPreferenceList() {
            List<PreferenceDTO> emptyList = Collections.emptyList();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> preferenceService.savePreferences(emptyList)
            );

            assertEquals("Preference list cannot be empty", exception.getMessage());
            verifyNoInteractions(studentRepository, preferenceRepository, optionalCourseRepository);
        }

        @Test
        @DisplayName("Should throw exception when student not found")
        void testStudentNotFound() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 999L, 100L)
            );

            when(studentRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> preferenceService.savePreferences(prefs)
            );

            assertEquals("Student not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when course not found")
        void testCourseNotFound() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 999L)
            );

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> preferenceService.savePreferences(prefs)
            );

            assertEquals("Optional course not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should save preferences successfully")
        void testSavePreferencesSuccess() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 100L),
                    new PreferenceDTO(2, 1L, 101L)
            );

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(100L)).thenReturn(Optional.of(testCourse1));
            when(optionalCourseRepository.findById(101L)).thenReturn(Optional.of(testCourse2));

            assertDoesNotThrow(() -> preferenceService.savePreferences(prefs));

            verify(preferenceRepository).deleteByStudentId(1L);
            verify(preferenceRepository).saveAll(argThat(savedPrefs -> {
                List<Preference> list = (List<Preference>) savedPrefs;
                return list.size() == 2;
            }));
        }

        @Test
        @DisplayName("Should delete old preferences before saving new ones")
        void testDeleteOldPreferencesFirst() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 100L)
            );

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(100L)).thenReturn(Optional.of(testCourse1));

            preferenceService.savePreferences(prefs);

            InOrder inOrder = inOrder(preferenceRepository);
            inOrder.verify(preferenceRepository).deleteByStudentId(1L);
            inOrder.verify(preferenceRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Should handle multiple preferences with correct priorities")
        void testMultiplePreferencesWithPriorities() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(3, 1L, 100L),
                    new PreferenceDTO(1, 1L, 101L),
                    new PreferenceDTO(2, 1L, 100L)
            );

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(anyLong())).thenReturn(Optional.of(testCourse1));

            preferenceService.savePreferences(prefs);

            verify(preferenceRepository).saveAll(argThat(savedPrefs -> {
                List<Preference> list = (List<Preference>) savedPrefs;
                return list.size() == 3 &&
                        list.stream().anyMatch(p -> p.getPriority() == 1) &&
                        list.stream().anyMatch(p -> p.getPriority() == 2) &&
                        list.stream().anyMatch(p -> p.getPriority() == 3);
            }));
        }

        @Test
        @DisplayName("Should handle large number of preferences")
        void testLargeNumberOfPreferences() {
            List<PreferenceDTO> prefs = IntStream.rangeClosed(1, 50)
                    .mapToObj(i -> new PreferenceDTO(i, 1L, 100L + i))
                    .collect(Collectors.toList());

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(anyLong())).thenReturn(Optional.of(testCourse1));

            assertDoesNotThrow(() -> preferenceService.savePreferences(prefs));

            verify(preferenceRepository).saveAll(argThat(savedPrefs -> {
                List<Preference> list = (List<Preference>) savedPrefs;
                return list.size() == 50;
            }));
        }
    }

    // ==================== GET PREFERENCES TESTS ====================

    @Nested
    @DisplayName("getPreferencesByStudentId() Tests")
    class GetPreferencesTests {

        @Test
        @DisplayName("Should return empty list when no preferences exist")
        void testNoPreferences() {
            when(preferenceRepository.findByStudentIdOrderByPriority(1L))
                    .thenReturn(Collections.emptyList());

            List<Preference> result = preferenceService.getPreferencesByStudentId(1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return preferences ordered by priority")
        void testPreferencesOrderedByPriority() {
            Preference pref1 = Preference.builder().priority(1).student(testStudent).optionalCourse(testCourse1).build();
            Preference pref2 = Preference.builder().priority(2).student(testStudent).optionalCourse(testCourse2).build();

            when(preferenceRepository.findByStudentIdOrderByPriority(1L))
                    .thenReturn(List.of(pref1, pref2));

            List<Preference> result = preferenceService.getPreferencesByStudentId(1L);

            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getPriority());
            assertEquals(2, result.get(1).getPriority());
        }

        @Test
        @DisplayName("Should handle non-existent student gracefully")
        void testNonExistentStudent() {
            when(preferenceRepository.findByStudentIdOrderByPriority(999L))
                    .thenReturn(Collections.emptyList());

            List<Preference> result = preferenceService.getPreferencesByStudentId(999L);

            assertTrue(result.isEmpty());
        }
    }

    // ==================== DELETE PREFERENCES TESTS ====================

    @Nested
    @DisplayName("deletePreferences() Tests")
    class DeletePreferencesTests {

        @Test
        @DisplayName("Should delete all preferences for student")
        void testDeletePreferences() {
            preferenceService.deletePreferences(1L);

            verify(preferenceRepository).deleteByStudentId(1L);
        }

        @Test
        @DisplayName("Should not throw when deleting non-existent preferences")
        void testDeleteNonExistentPreferences() {
            doNothing().when(preferenceRepository).deleteByStudentId(999L);

            assertDoesNotThrow(() -> preferenceService.deletePreferences(999L));
        }
    }

    // ==================== BEHAVIORAL TESTS ====================

    @Nested
    @DisplayName("Behavioral Tests")
    class BehavioralTests {

        @Test
        @DisplayName("Should use first student ID from list for deletion")
        void testUsesFirstStudentIdForDeletion() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 100L),
                    new PreferenceDTO(2, 2L, 101L) // Different student ID (edge case)
            );

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(anyLong())).thenReturn(Optional.of(testCourse1));

            preferenceService.savePreferences(prefs);

            // Should delete based on first DTO's student ID
            verify(preferenceRepository).deleteByStudentId(1L);
        }

        @Test
        @DisplayName("Should create preference with correct associations")
        void testCorrectAssociations() {
            List<PreferenceDTO> prefs = List.of(
                    new PreferenceDTO(1, 1L, 100L)
            );

            when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
            when(optionalCourseRepository.findById(100L)).thenReturn(Optional.of(testCourse1));

            preferenceService.savePreferences(prefs);

            verify(preferenceRepository).saveAll(argThat(savedPrefs -> {
                List<Preference> list = (List<Preference>) savedPrefs;
                Preference pref = list.get(0);
                return pref.getStudent().equals(testStudent) &&
                        pref.getOptionalCourse().equals(testCourse1) &&
                        pref.getPriority() == 1;
            }));
        }
    }

    // ==================== HELPER METHODS ====================

    private void setId(Student student, Long id) {
        try {
            var field = student.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(student, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setIdForCourse(OptionalCourse course, Long id) {
        try {
            var field = course.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(course, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

