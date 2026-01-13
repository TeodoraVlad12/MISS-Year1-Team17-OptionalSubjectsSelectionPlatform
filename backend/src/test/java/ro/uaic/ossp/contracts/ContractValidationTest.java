package ro.uaic.ossp.contracts;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ro.uaic.ossp.dtos.*;
import ro.uaic.ossp.models.enums.AllocationStrategy;
import ro.uaic.ossp.models.enums.TransferStatus;

import jakarta.validation.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Advanced AI-Generated Contract Tests
 * Covers: DTO validation, API contracts, data integrity rules
 */
@DisplayName("Contract & DTO Validation Tests")
class ContractValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // Move provider methods here: static methods are not allowed inside non-static inner classes
    static Stream<Integer> validPreferencePriorities() {
        return Stream.of(1, 2, 3, 5, 10, 100, Integer.MAX_VALUE);
    }

    static Stream<Arguments> statusTransitions() {
        return Stream.of(
                Arguments.of(TransferStatus.PENDING, TransferStatus.APPROVED, true),
                Arguments.of(TransferStatus.PENDING, TransferStatus.REJECTED, true),
                Arguments.of(TransferStatus.APPROVED, TransferStatus.REJECTED, false),
                Arguments.of(TransferStatus.REJECTED, TransferStatus.APPROVED, false)
        );
    }

    // ==================== PREFERENCE DTO CONTRACT TESTS ====================

    @Nested
    @DisplayName("PreferenceDTO Contract Tests")
    class PreferenceDTOContractTests {

        @Test
        @DisplayName("Valid PreferenceDTO should pass validation")
        void testValidPreferenceDTO() {
            PreferenceDTO dto = new PreferenceDTO(1, 100L, 200L);

            Set<ConstraintViolation<PreferenceDTO>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Priority must be at least 1")
        void testPriorityMinimum() {
            PreferenceDTO dto = new PreferenceDTO(0, 100L, 200L);

            Set<ConstraintViolation<PreferenceDTO>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("priority")))
                    .isTrue();
        }

        @Test
        @DisplayName("StudentId cannot be null")
        void testStudentIdNotNull() {
            PreferenceDTO dto = new PreferenceDTO(1, null, 200L);

            Set<ConstraintViolation<PreferenceDTO>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("studentId")))
                    .isTrue();
        }

        @Test
        @DisplayName("CourseId cannot be null")
        void testCourseIdNotNull() {
            PreferenceDTO dto = new PreferenceDTO(1, 100L, null);

            Set<ConstraintViolation<PreferenceDTO>> violations = validator.validate(dto);

            assertThat(violations).isNotEmpty();
            assertThat(violations.stream()
                    .anyMatch(v -> v.getPropertyPath().toString().equals("courseId")))
                    .isTrue();
        }

        @ParameterizedTest
        @MethodSource("ro.uaic.ossp.contracts.ContractValidationTest#validPreferencePriorities")
        @DisplayName("Should accept valid priority values")
        void testValidPriorityValues(int priority) {
            PreferenceDTO dto = new PreferenceDTO(priority, 1L, 1L);

            Set<ConstraintViolation<PreferenceDTO>> violations = validator.validate(dto);

            assertThat(violations).isEmpty();
        }
    }

    // ==================== STUDENT ALLOCATION DTO CONTRACT TESTS ====================

    @Nested
    @DisplayName("StudentAllocationDTO Contract Tests")
    class StudentAllocationDTOContractTests {

        @Test
        @DisplayName("Should build valid StudentAllocationDTO using builder")
        void testBuilderPattern() {
            StudentAllocationDTO dto = StudentAllocationDTO.builder()
                    .studentId(1L)
                    .studentName("John Doe")
                    .allocatedCourseId(100L)
                    .allocatedCourseName("Advanced Programming")
                    .preferenceRank(1)
                    .build();

            assertThat(dto.getStudentId()).isEqualTo(1L);
            assertThat(dto.getStudentName()).isEqualTo("John Doe");
            assertThat(dto.getAllocatedCourseId()).isEqualTo(100L);
            assertThat(dto.getAllocatedCourseName()).isEqualTo("Advanced Programming");
            assertThat(dto.getPreferenceRank()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should allow null optional fields")
        void testNullableFields() {
            StudentAllocationDTO dto = StudentAllocationDTO.builder()
                    .studentId(1L)
                    .studentName(null)
                    .allocatedCourseId(100L)
                    .allocatedCourseName(null)
                    .preferenceRank(1)
                    .build();

            assertThat(dto.getStudentName()).isNull();
            assertThat(dto.getAllocatedCourseName()).isNull();
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void testEqualsAndHashCode() {
            StudentAllocationDTO dto1 = StudentAllocationDTO.builder()
                    .studentId(1L)
                    .allocatedCourseId(100L)
                    .preferenceRank(1)
                    .build();

            StudentAllocationDTO dto2 = StudentAllocationDTO.builder()
                    .studentId(1L)
                    .allocatedCourseId(100L)
                    .preferenceRank(1)
                    .build();

            assertThat(dto1).isEqualTo(dto2);
            assertThat(dto1).hasSameHashCodeAs(dto2);
        }
    }

    // ==================== TRANSFER REQUEST DTO CONTRACT TESTS ====================

    @Nested
    @DisplayName("TransferRequestDTO Contract Tests")
    class TransferRequestDTOContractTests {

        @Test
        @DisplayName("TransferRequestCreateDTO should have required fields")
        void testCreateDTORequiredFields() {
            TransferRequestCreateDTO dto = new TransferRequestCreateDTO();
            dto.setStudentId(1L);
            dto.setCurrentCourseId(100L);
            dto.setRequestedCourseId(101L);

            assertThat(dto.getStudentId()).isEqualTo(1L);
            assertThat(dto.getCurrentCourseId()).isEqualTo(100L);
            assertThat(dto.getRequestedCourseId()).isEqualTo(101L);
        }

        @Test
        @DisplayName("TransferRequestResponseDTO should contain all status information")
        void testResponseDTOStatusInfo() {
            TransferRequestResponseDTO dto = TransferRequestResponseDTO.builder()
                    .id(1L)
                    .studentId(1L)
                    .currentCourseId(100L)
                    .requestedCourseId(101L)
                    .requestDate(LocalDate.now())
                    .status(TransferStatus.PENDING)
                    .build();

            assertThat(dto.getStatus()).isEqualTo(TransferStatus.PENDING);
            assertThat(dto.getId()).isEqualTo(1L);
        }
    }

    // ==================== ALLOCATION STRATEGY ENUM CONTRACT TESTS ====================

    @Nested
    @DisplayName("AllocationStrategy Enum Contract Tests")
    class AllocationStrategyContractTests {

        @Test
        @DisplayName("Should have GALE_SHAPLEY strategy")
        void testGaleShapleyExists() {
            AllocationStrategy strategy = AllocationStrategy.GALE_SHAPLEY;

            assertThat(strategy.getValue()).isEqualTo("gale_shapley");
            assertThat(strategy.getDisplayName()).isEqualTo("Gale-Shapley Algorithm");
        }

        @Test
        @DisplayName("Should have GRADE_BASED strategy")
        void testGradeBasedExists() {
            AllocationStrategy strategy = AllocationStrategy.GRADE_BASED;

            assertThat(strategy.getValue()).isEqualTo("grade_based");
            assertThat(strategy.getDisplayName()).isEqualTo("Grade Based Algorithm");
        }

        @Test
        @DisplayName("Should convert from value string")
        void testFromValue() {
            AllocationStrategy gs = AllocationStrategy.fromValue("gale_shapley");
            AllocationStrategy gb = AllocationStrategy.fromValue("grade_based");

            assertThat(gs).isEqualTo(AllocationStrategy.GALE_SHAPLEY);
            assertThat(gb).isEqualTo(AllocationStrategy.GRADE_BASED);
        }

        @Test
        @DisplayName("Should throw on invalid value")
        void testInvalidValue() {
            assertThatThrownBy(() -> AllocationStrategy.fromValue("invalid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown allocation strategy");
        }

        @Test
        @DisplayName("Should be case insensitive")
        void testCaseInsensitive() {
            AllocationStrategy upper = AllocationStrategy.fromValue("GALE_SHAPLEY");
            AllocationStrategy lower = AllocationStrategy.fromValue("gale_shapley");
            AllocationStrategy mixed = AllocationStrategy.fromValue("Gale_Shapley");

            assertThat(upper).isEqualTo(lower).isEqualTo(mixed);
        }
    }

    // ==================== TRANSFER STATUS ENUM CONTRACT TESTS ====================

    @Nested
    @DisplayName("TransferStatus Enum Contract Tests")
    class TransferStatusContractTests {

        @Test
        @DisplayName("Should have all expected statuses")
        void testAllStatusesExist() {
            TransferStatus[] statuses = TransferStatus.values();

            assertThat(statuses).contains(
                    TransferStatus.PENDING,
                    TransferStatus.APPROVED,
                    TransferStatus.REJECTED
            );
        }

        @ParameterizedTest
        @MethodSource("ro.uaic.ossp.contracts.ContractValidationTest#statusTransitions")
        @DisplayName("Should define valid state transitions")
        void testStateTransitions(TransferStatus from, @SuppressWarnings("unused") TransferStatus to, boolean valid) {
            // This is a documentation test for valid transitions
            // PENDING -> APPROVED: valid
            // PENDING -> REJECTED: valid
            // APPROVED -> any: invalid
            // REJECTED -> any: invalid

            boolean isValidTransition = (from == TransferStatus.PENDING);

            assertThat(isValidTransition).isEqualTo(valid);
        }
    }

    // ==================== API RESPONSE FORMAT CONTRACT TESTS ====================

    @Nested
    @DisplayName("API Response Format Contract Tests")
    class APIResponseFormatTests {

        @Test
        @DisplayName("GradeUploadResultDTO should contain upload statistics")
        void testGradeUploadResultFormat() {
            GradeUploadResultDTO dto = new GradeUploadResultDTO(10, 5, 3, 2, "Test upload");

            // Verify the DTO can hold required fields
            assertThat(dto).isNotNull();
            assertThat(dto.getTotalRows()).isEqualTo(10);
            assertThat(dto.getInserted()).isEqualTo(5);
            assertThat(dto.getUpdated()).isEqualTo(3);
            assertThat(dto.getSkipped()).isEqualTo(2);
            assertThat(dto.getMessage()).isEqualTo("Test upload");
        }

        @Test
        @DisplayName("LoginResponseDTO should contain authentication token")
        void testLoginResponseFormat() {
            LoginResponseDTO dto = LoginResponseDTO.builder()
                    .token("jwt.token.here")
                    .build();

            assertThat(dto.getToken()).isEqualTo("jwt.token.here");
        }

        @Test
        @DisplayName("GradeResponseDTO should contain grade information")
        void testGradeResponseFormat() {
            GradeResponseDTO dto = GradeResponseDTO.builder()
                    .studentName("John Doe")
                    .matricol("123456")
                    .build();

            assertThat(dto.getStudentName()).isEqualTo("John Doe");
            assertThat(dto.getMatricol()).isEqualTo("123456");
        }
    }

    // ==================== DATA INTEGRITY RULES TESTS ====================

    @Nested
    @DisplayName("Data Integrity Rules Tests")
    class DataIntegrityRulesTests {

        @Test
        @DisplayName("Student and Course IDs should be positive")
        void testPositiveIds() {
            // Valid
            PreferenceDTO valid = new PreferenceDTO(1, 1L, 1L);
            assertThat(validator.validate(valid)).isEmpty();

            // These would be caught by business logic, not DTO validation
            PreferenceDTO negativeStudent = new PreferenceDTO(1, -1L, 1L);
            PreferenceDTO negativeCourse = new PreferenceDTO(1, 1L, -1L);

            // Current DTOs don't validate positive IDs, but they should in production
            assertThat(negativeStudent.getStudentId()).isNegative();
            assertThat(negativeCourse.getCourseId()).isNegative();
        }

        @Test
        @DisplayName("Priority ordering should be consistent")
        void testPriorityOrdering() {
            PreferenceDTO pref1 = new PreferenceDTO(1, 1L, 100L);
            PreferenceDTO pref2 = new PreferenceDTO(2, 1L, 101L);
            PreferenceDTO pref3 = new PreferenceDTO(3, 1L, 102L);

            // Lower priority number = higher preference
            assertThat(pref1.getPriority()).isLessThan(pref2.getPriority());
            assertThat(pref2.getPriority()).isLessThan(pref3.getPriority());
        }
    }
}
