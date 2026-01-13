package ro.uaic.ossp.services;

import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ro.uaic.ossp.dtos.TransferRequestResponseDTO;
import ro.uaic.ossp.models.Enrollment;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.models.TransferRequest;
import ro.uaic.ossp.models.enums.TransferStatus;
import ro.uaic.ossp.repositories.EnrollmentRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;
import ro.uaic.ossp.repositories.StudentRepository;
import ro.uaic.ossp.repositories.TransferRequestRepository;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.security.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransferRequestService {
    private final TransferRequestRepository transferRepo;
    private final StudentRepository studentRepo;
    private final OptionalCourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final TransferValidator validator; // NEW: Extract validation
    private final TransferProcessor processor; // NEW: Extract business logic

    @Transactional
    public TransferRequestResponseDTO create(Long studentId, Long currentCourseId, Long requestedCourseId) {
        TransferRequestData data = validator.validateAndPrepareData(
                studentId, currentCourseId, requestedCourseId); // EXTRACT METHOD

        TransferRequest tr = buildTransferRequest(data);
        transferRepo.save(tr);

        return TransferDtoMapper.toDto(tr); // EXTRACT METHOD
    }

    private TransferRequest buildTransferRequest(TransferRequestData data) {
        return TransferRequest.builder()
                .student(data.getStudent())
                .currentCourse(data.getCurrentCourse())
                .requestedCourse(data.getRequestedCourse())
                .requestDate(LocalDate.now())
                .status(TransferStatus.PENDING)
                .build();
    }

    @Transactional
    public TransferRequestResponseDTO approve(Long id) {
        TransferRequest tr = findTransferRequestOrThrow(id);
        validator.validatePendingStatus(tr); // EXTRACT METHOD

        processor.executeTransfer(tr); // EXTRACT METHOD

        tr.setStatus(TransferStatus.APPROVED);
        transferRepo.save(tr);

        return TransferDtoMapper.toDto(tr);
    }

    @Transactional
    public TransferRequestResponseDTO reject(Long id) {
        TransferRequest tr = findTransferRequestOrThrow(id);
        tr.setStatus(TransferStatus.REJECTED);
        transferRepo.save(tr);

        return TransferDtoMapper.toDto(tr);
    }

    private TransferRequest findTransferRequestOrThrow(Long id) {
        return transferRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Transfer request not found"));
    }

    // New methods required by the controller

    public List<TransferRequestResponseDTO> getAll() {
        return transferRepo.findAll().stream()
                .map(TransferDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public TransferRequestResponseDTO getById(Long id) {
        return TransferDtoMapper.toDto(findTransferRequestOrThrow(id));
    }

    @Transactional
    public TransferRequestResponseDTO update(Long id, Long requestedCourseId) {
        TransferRequest tr = findTransferRequestOrThrow(id);
        validator.validatePendingStatus(tr);

        OptionalCourse requestedCourse = courseRepo.findById(requestedCourseId)
                .orElseThrow(() -> new NotFoundException("Requested course not found"));

        tr.setRequestedCourse(requestedCourse);
        transferRepo.save(tr);
        return TransferDtoMapper.toDto(tr);
    }

    @Transactional
    public void delete(Long id) {
        TransferRequest tr = findTransferRequestOrThrow(id);
        transferRepo.deleteById(tr.getId());
    }

    public List<TransferRequestResponseDTO> getByStudent(Long studentId) {
        return transferRepo.findByStudentId(studentId).stream()
                .map(TransferDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TransferRequestResponseDTO> getByStatus(TransferStatus status) {
        return transferRepo.findByStatus(status).stream()
                .map(TransferDtoMapper::toDto)
                .collect(Collectors.toList());
    }

}

// NEW: Data class to eliminate data clumps
@Getter
@Builder
class TransferRequestData {
    private final Student student;
    private final OptionalCourse currentCourse;
    private final OptionalCourse requestedCourse;
}

// NEW: Validator class
@Component
@RequiredArgsConstructor
class TransferValidator {
    private final StudentRepository studentRepo;
    private final OptionalCourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    public TransferRequestData validateAndPrepareData(Long studentId, Long currentCourseId,
                                                      Long requestedCourseId) {
        Student student = findStudentOrThrow(studentId);
        OptionalCourse currentCourse = findCourseOrThrow(currentCourseId);
        OptionalCourse requestedCourse = findCourseOrThrow(requestedCourseId);

        validateStudentEnrollment(studentId, currentCourseId);

        return TransferRequestData.builder()
                .student(student)
                .currentCourse(currentCourse)
                .requestedCourse(requestedCourse)
                .build();
    }

    public void validatePendingStatus(TransferRequest tr) {
        if (tr.getStatus() != TransferStatus.PENDING) {
            throw new BadRequestException("Only requests in PENDING can be approved.");
        }
    }

    private Student findStudentOrThrow(Long studentId) {
        return studentRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    private OptionalCourse findCourseOrThrow(Long courseId) {
        return courseRepo.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private void validateStudentEnrollment(Long studentId, Long courseId) {
        enrollmentRepo.findByStudentIdAndOptionalCourseId(studentId, courseId)
                .orElseThrow(() -> new BadRequestException(
                        "The student is not enrolled in the specified course"
                ));
    }
}

// NEW: Processor for business logic
@Component
@RequiredArgsConstructor
class TransferProcessor {
    private final EnrollmentRepository enrollmentRepo;

    public void executeTransfer(TransferRequest tr) {
        Enrollment currentEnrollment = findCurrentEnrollment(tr);
        enrollmentRepo.delete(currentEnrollment);

        Enrollment newEnrollment = createNewEnrollment(tr);
        enrollmentRepo.save(newEnrollment);
    }

    private Enrollment findCurrentEnrollment(TransferRequest tr) {
        return enrollmentRepo
                .findByStudentIdAndOptionalCourseId(
                        tr.getStudent().getId(),
                        tr.getCurrentCourse().getId())
                .orElseThrow(() -> new BadRequestException(
                        "Student no longer enrolled in original course"
                ));
    }

    private Enrollment createNewEnrollment(TransferRequest tr) {
        return Enrollment.builder()
                .student(tr.getStudent())
                .optionalCourse(tr.getRequestedCourse())
                .build();
    }
}

// NEW: DTO mapper
class TransferDtoMapper {
    public static TransferRequestResponseDTO toDto(TransferRequest tr) {
        return TransferRequestResponseDTO.builder()
                .id(tr.getId())
                .studentId(tr.getStudent().getId())
                .currentCourseId(tr.getCurrentCourse().getId())
                .requestedCourseId(tr.getRequestedCourse().getId())
                .requestDate(tr.getRequestDate())
                .status(tr.getStatus())
                .build();
    }
}
