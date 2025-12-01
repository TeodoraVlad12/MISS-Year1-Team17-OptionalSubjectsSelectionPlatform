package ro.uaic.ossp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class TransferRequestService {

    private final TransferRequestRepository transferRepo;
    private final StudentRepository studentRepo;
    private final OptionalCourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    // -----------------------------
    // CREATE REQUEST
    // -----------------------------
    @Transactional
    public TransferRequestResponseDTO create(Long studentId, Long currentCourseId, Long requestedCourseId) {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        OptionalCourse currentCourse = courseRepo.findById(currentCourseId)
                .orElseThrow(() -> new NotFoundException("Current course not found"));

        OptionalCourse requestedCourse = courseRepo.findById(requestedCourseId)
                .orElseThrow(() -> new NotFoundException("Requested course not found"));

        // VALIDATION: the student must be enrolled in the current course
        enrollmentRepo.findByStudentIdAndOptionalCourseId(studentId, currentCourseId)
                .orElseThrow(() -> new BadRequestException(
                        "The student is not enrolled in: " + currentCourse.getName()
                ));

        TransferRequest tr = TransferRequest.builder()
                .student(student)
                .currentCourse(currentCourse)
                .requestedCourse(requestedCourse)
                .requestDate(LocalDate.now())
                .status(TransferStatus.PENDING)
                .build();

        transferRepo.save(tr);

        return buildDTO(tr);
    }

    // -----------------------------
    // GET ALL
    // -----------------------------
    public List<TransferRequestResponseDTO> getAll() {
        return transferRepo.findAll().stream().map(this::buildDTO).toList();
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    public TransferRequestResponseDTO getById(Long id) {
        return buildDTO(
                transferRepo.findById(id)
                        .orElseThrow(() -> new NotFoundException("Transfer request not found"))
        );
    }

    // -----------------------------
    // UPDATE REQUEST
    // -----------------------------
    @Transactional
    public TransferRequestResponseDTO update(Long id, Long newRequestedCourseId) {
        TransferRequest tr = transferRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Transfer request not found"));

        if (tr.getStatus() != TransferStatus.PENDING)
            throw new BadRequestException("Only requests in PENDING can be modified.");

        OptionalCourse newCourse = courseRepo.findById(newRequestedCourseId)
                .orElseThrow(() -> new NotFoundException("Requested course not found"));

        tr.setRequestedCourse(newCourse);
        transferRepo.save(tr);

        return buildDTO(tr);
    }

    // -----------------------------
    // DELETE REQUEST
    // -----------------------------
    @Transactional
    public void delete(Long id) {
        TransferRequest tr = transferRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Transfer request not found"));

        if (tr.getStatus() != TransferStatus.PENDING)
            throw new BadRequestException("Only requests in PENDING can be deleted.");

        transferRepo.delete(tr);
    }

    // -----------------------------
    // FILTER BY STUDENT
    // -----------------------------
    public List<TransferRequestResponseDTO> getByStudent(Long studentId) {
        return transferRepo.findByStudentId(studentId)
                .stream().map(this::buildDTO).toList();
    }

    // -----------------------------
    // FILTER BY STATUS
    // -----------------------------
    public List<TransferRequestResponseDTO> getByStatus(TransferStatus status) {
        return transferRepo.findByStatus(status)
                .stream().map(this::buildDTO).toList();
    }

    // -----------------------------
    // APPROVE TRANSFER (performs the actual transfer)
    // -----------------------------
    @Transactional
    public TransferRequestResponseDTO approve(Long id) {
        TransferRequest tr = transferRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Transfer request not found"));

        if (tr.getStatus() != TransferStatus.PENDING)
            throw new BadRequestException("Only requests in PENDING can be approved.");

        Long studentId = tr.getStudent().getId();
        Long currentCourseId = tr.getCurrentCourse().getId();
        Long requestedCourseId = tr.getRequestedCourse().getId();

        // VALIDATION: the student must be enrolled in the current course
        Enrollment currentEnrollment = enrollmentRepo
                .findByStudentIdAndOptionalCourseId(studentId, currentCourseId)
                .orElseThrow(() -> new BadRequestException(
                        "The student is no longer enrolled in the original course — the request cannot be approved"
                ));

        // PERFORM TRANSFER
        enrollmentRepo.delete(currentEnrollment);

        Enrollment newEnrollment = Enrollment.builder()
                .student(tr.getStudent())
                .optionalCourse(tr.getRequestedCourse())
                .build();

        enrollmentRepo.save(newEnrollment);

        tr.setStatus(TransferStatus.APPROVED);
        transferRepo.save(tr);

        return buildDTO(tr);
    }

    // -----------------------------
    // REJECT TRANSFER
    // -----------------------------
    @Transactional
    public TransferRequestResponseDTO reject(Long id) {
        TransferRequest tr = transferRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Transfer request not found"));

        tr.setStatus(TransferStatus.REJECTED);
        transferRepo.save(tr);

        return buildDTO(tr);
    }

    // -----------------------------
    // HELPER – build DTO
    // -----------------------------
    private TransferRequestResponseDTO buildDTO(TransferRequest tr) {
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
