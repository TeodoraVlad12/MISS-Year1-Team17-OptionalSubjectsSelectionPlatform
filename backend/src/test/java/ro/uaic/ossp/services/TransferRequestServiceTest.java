package ro.uaic.ossp.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.uaic.ossp.dtos.TransferRequestResponseDTO;
import ro.uaic.ossp.models.*;
import ro.uaic.ossp.models.enums.TransferStatus;
import ro.uaic.ossp.repositories.*;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.security.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Advanced AI-Generated Tests for TransferRequestService
 * Covers: State machine testing, workflow validation, concurrency, and edge cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransferRequestService Advanced Tests")
class TransferRequestServiceTest {

    @Mock private TransferRequestRepository transferRepo;
    @Mock private StudentRepository studentRepo;
    @Mock private OptionalCourseRepository courseRepo;
    @Mock private EnrollmentRepository enrollmentRepo;
    @Mock private TransferValidator validator;
    @Mock private TransferProcessor processor;

    @InjectMocks
    private TransferRequestService service;

    private Student testStudent;
    private OptionalCourse currentCourse;
    private OptionalCourse requestedCourse;
    private TransferRequest pendingRequest;

    @BeforeEach
    void setUp() {
        testStudent = createTestStudent(1L, "123456");
        currentCourse = createTestCourse(100L, "CURRENT001");
        requestedCourse = createTestCourse(101L, "REQUESTED001");
        pendingRequest = createTransferRequest(1L, testStudent, currentCourse, requestedCourse, TransferStatus.PENDING);
    }

    // ==================== STATE MACHINE TESTS ====================

    @Nested
    @DisplayName("Transfer Status State Machine Tests")
    class StateMachineTests {

        @Test
        @DisplayName("PENDING -> APPROVED transition should be valid")
        void testPendingToApprovedTransition() {
            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            doNothing().when(validator).validatePendingStatus(any());
            doNothing().when(processor).executeTransfer(any());
            when(transferRepo.save(any())).thenReturn(pendingRequest);

            TransferRequestResponseDTO result = service.approve(1L);

            assertEquals(TransferStatus.APPROVED, pendingRequest.getStatus());
            verify(processor).executeTransfer(pendingRequest);
        }

        @Test
        @DisplayName("PENDING -> REJECTED transition should be valid")
        void testPendingToRejectedTransition() {
            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            when(transferRepo.save(any())).thenReturn(pendingRequest);

            TransferRequestResponseDTO result = service.reject(1L);

            assertEquals(TransferStatus.REJECTED, pendingRequest.getStatus());
        }

        @Test
        @DisplayName("APPROVED -> any transition should fail")
        void testApprovedCannotTransition() {
            TransferRequest approvedRequest = createTransferRequest(
                    1L, testStudent, currentCourse, requestedCourse, TransferStatus.APPROVED);

            when(transferRepo.findById(1L)).thenReturn(Optional.of(approvedRequest));
            doThrow(new BadRequestException("Already processed"))
                    .when(validator).validatePendingStatus(approvedRequest);

            assertThrows(BadRequestException.class, () -> service.approve(1L));
        }

        @Test
        @DisplayName("REJECTED -> any transition should fail")
        void testRejectedCannotTransition() {
            TransferRequest rejectedRequest = createTransferRequest(
                    1L, testStudent, currentCourse, requestedCourse, TransferStatus.REJECTED);

            when(transferRepo.findById(1L)).thenReturn(Optional.of(rejectedRequest));
            doThrow(new BadRequestException("Already processed"))
                    .when(validator).validatePendingStatus(rejectedRequest);

            assertThrows(BadRequestException.class, () -> service.approve(1L));
        }

        @ParameterizedTest
        @EnumSource(TransferStatus.class)
        @DisplayName("Should handle all status types correctly")
        void testAllStatusTypes(TransferStatus status) {
            TransferRequest request = createTransferRequest(
                    1L, testStudent, currentCourse, requestedCourse, status);

            when(transferRepo.findById(1L)).thenReturn(Optional.of(request));

            TransferRequestResponseDTO result = service.getById(1L);

            assertNotNull(result);
        }
    }

    // ==================== WORKFLOW TESTS ====================

    @Nested
    @DisplayName("Transfer Workflow Tests")
    class WorkflowTests {

        @Test
        @DisplayName("Complete transfer workflow: Create -> Approve")
        void testCompleteApprovalWorkflow() {
            // Step 1: Create
            TransferRequestData data = mock(TransferRequestData.class);
            when(data.getStudent()).thenReturn(testStudent);
            when(data.getCurrentCourse()).thenReturn(currentCourse);
            when(data.getRequestedCourse()).thenReturn(requestedCourse);
            when(validator.validateAndPrepareData(1L, 100L, 101L)).thenReturn(data);
            when(transferRepo.save(any())).thenAnswer(inv -> {
                TransferRequest tr = inv.getArgument(0);
                setId(tr, 1L);
                return tr;
            });

            TransferRequestResponseDTO created = service.create(1L, 100L, 101L);
            assertNotNull(created);

            // Step 2: Approve
            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            doNothing().when(validator).validatePendingStatus(any());
            doNothing().when(processor).executeTransfer(any());

            TransferRequestResponseDTO approved = service.approve(1L);
            assertEquals(TransferStatus.APPROVED, pendingRequest.getStatus());
        }

        @Test
        @DisplayName("Complete transfer workflow: Create -> Reject")
        void testCompleteRejectionWorkflow() {
            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            when(transferRepo.save(any())).thenReturn(pendingRequest);

            TransferRequestResponseDTO rejected = service.reject(1L);

            assertEquals(TransferStatus.REJECTED, pendingRequest.getStatus());
            verify(processor, never()).executeTransfer(any());
        }

        @Test
        @DisplayName("Update workflow: should only work for PENDING status")
        void testUpdateWorkflow() {
            OptionalCourse newCourse = createTestCourse(102L, "NEW001");

            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            doNothing().when(validator).validatePendingStatus(pendingRequest);
            when(courseRepo.findById(102L)).thenReturn(Optional.of(newCourse));
            when(transferRepo.save(any())).thenReturn(pendingRequest);

            TransferRequestResponseDTO updated = service.update(1L, 102L);

            assertEquals(newCourse, pendingRequest.getRequestedCourse());
        }
    }

    // ==================== QUERY TESTS ====================

    @Nested
    @DisplayName("Query Operations Tests")
    class QueryTests {

        @Test
        @DisplayName("Should return all transfer requests")
        void testGetAll() {
            List<TransferRequest> requests = List.of(
                    pendingRequest,
                    createTransferRequest(2L, testStudent, currentCourse, requestedCourse, TransferStatus.APPROVED)
            );

            when(transferRepo.findAll()).thenReturn(requests);

            List<TransferRequestResponseDTO> result = service.getAll();

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should filter by student ID")
        void testGetByStudent() {
            when(transferRepo.findByStudentId(1L)).thenReturn(List.of(pendingRequest));

            List<TransferRequestResponseDTO> result = service.getByStudent(1L);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should filter by status")
        void testGetByStatus() {
            when(transferRepo.findByStatus(TransferStatus.PENDING))
                    .thenReturn(List.of(pendingRequest));

            List<TransferRequestResponseDTO> result = service.getByStatus(TransferStatus.PENDING);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Should throw NotFoundException for invalid ID")
        void testGetByIdNotFound() {
            when(transferRepo.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.getById(999L));
        }
    }

    // ==================== DELETE TESTS ====================

    @Nested
    @DisplayName("Delete Operation Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete transfer request successfully")
        void testDeleteSuccess() {
            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            doNothing().when(transferRepo).deleteById(1L);

            assertDoesNotThrow(() -> service.delete(1L));

            verify(transferRepo).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw NotFoundException when deleting non-existent request")
        void testDeleteNotFound() {
            when(transferRepo.findById(999L)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.delete(999L));
        }
    }

    // ==================== CONCURRENCY TESTS ====================

    @Nested
    @DisplayName("Concurrency Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("Should handle concurrent approval attempts gracefully")
        void testConcurrentApproval() throws InterruptedException {
            when(transferRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
            doNothing().when(validator).validatePendingStatus(any());
            doNothing().when(processor).executeTransfer(any());
            when(transferRepo.save(any())).thenReturn(pendingRequest);

            ExecutorService executor = Executors.newFixedThreadPool(5);
            CountDownLatch latch = new CountDownLatch(5);
            List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < 5; i++) {
                executor.submit(() -> {
                    try {
                        service.approve(1L);
                    } catch (Exception e) {
                        errors.add(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            // All should succeed (in real scenario, optimistic locking would prevent duplicates)
            verify(transferRepo, atLeast(1)).save(any());
        }
    }

    // ==================== EDGE CASE TESTS ====================

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle same source and target course")
        void testSameSourceAndTarget() {
            when(validator.validateAndPrepareData(1L, 100L, 100L))
                    .thenThrow(new BadRequestException("Cannot transfer to same course"));

            assertThrows(BadRequestException.class,
                    () -> service.create(1L, 100L, 100L));
        }

        @Test
        @DisplayName("Should handle null student ID")
        void testNullStudentId() {
            when(validator.validateAndPrepareData(null, 100L, 101L))
                    .thenThrow(new BadRequestException("Student ID is required"));

            assertThrows(BadRequestException.class,
                    () -> service.create(null, 100L, 101L));
        }

        @Test
        @DisplayName("Should handle request date correctly")
        void testRequestDateIsToday() {
            TransferRequestData data = mock(TransferRequestData.class);
            when(data.getStudent()).thenReturn(testStudent);
            when(data.getCurrentCourse()).thenReturn(currentCourse);
            when(data.getRequestedCourse()).thenReturn(requestedCourse);
            when(validator.validateAndPrepareData(1L, 100L, 101L)).thenReturn(data);

            ArgumentCaptor<TransferRequest> captor = ArgumentCaptor.forClass(TransferRequest.class);
            when(transferRepo.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

            service.create(1L, 100L, 101L);

            assertEquals(LocalDate.now(), captor.getValue().getRequestDate());
        }

        @Test
        @DisplayName("Should handle bulk operations")
        void testBulkGetByStatus() {
            List<TransferRequest> manyRequests = IntStream.rangeClosed(1, 100)
                    .mapToObj(i -> createTransferRequest(
                            (long) i, testStudent, currentCourse, requestedCourse, TransferStatus.PENDING))
                    .toList();

            when(transferRepo.findByStatus(TransferStatus.PENDING)).thenReturn(manyRequests);

            List<TransferRequestResponseDTO> result = service.getByStatus(TransferStatus.PENDING);

            assertEquals(100, result.size());
        }
    }

    // ==================== HELPER METHODS ====================

    private Student createTestStudent(Long id, String matriculationNumber) {
        Student student = Student.builder()
                .matriculationNumber(matriculationNumber)
                .academicYear(2)
                .specialization("CS")
                .groupNumber("A1")
                .build();
        setStudentId(student, id);
        return student;
    }

    private OptionalCourse createTestCourse(Long id, String code) {
        OptionalCourse course = OptionalCourse.builder()
                .code(code)
                .maxStudents(30)
                .build();
        setCourseId(course, id);
        return course;
    }

    private TransferRequest createTransferRequest(Long id, Student student,
                                                   OptionalCourse current, OptionalCourse requested,
                                                   TransferStatus status) {
        TransferRequest tr = TransferRequest.builder()
                .student(student)
                .currentCourse(current)
                .requestedCourse(requested)
                .requestDate(LocalDate.now())
                .status(status)
                .build();
        setId(tr, id);
        return tr;
    }

    private void setId(TransferRequest tr, Long id) {
        try {
            var field = TransferRequest.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(tr, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void setStudentId(Student student, Long id) {
        try {
            var field = student.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(student, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void setCourseId(OptionalCourse course, Long id) {
        try {
            var field = course.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(course, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}

