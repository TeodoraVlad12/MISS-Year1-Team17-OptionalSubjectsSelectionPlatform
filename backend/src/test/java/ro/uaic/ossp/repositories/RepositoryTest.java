package ro.uaic.ossp.repositories;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ro.uaic.ossp.models.*;
import ro.uaic.ossp.models.enums.TransferStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Advanced AI-Generated Repository Tests
 * Covers: JPA queries, transactions, lazy loading, cascade operations
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repository Layer Tests")
class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OptionalCourseRepository courseRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private TransferRequestRepository transferRequestRepository;

    @Autowired
    @SuppressWarnings("unused")
    private CoursePackageRepository packageRepository;

    private CoursePackage testPackage;

    @BeforeEach
    void setUp() {
        testPackage = new CoursePackage();
        testPackage.setName("Test Package");
        testPackage.setYear(1);
        testPackage.setSemester(1);
        testPackage.setLevel("Bachelor");
        entityManager.persist(testPackage);
        entityManager.flush();
    }

    // ==================== STUDENT REPOSITORY TESTS ====================

    @Nested
    @DisplayName("StudentRepository Tests")
    class StudentRepositoryTests {

        @Test
        @DisplayName("Should save and retrieve student by matriculation number")
        void testFindByMatriculationNumber() {
            Student student = Student.builder()
                    .matriculationNumber("MAT123456")
                    .academicYear(2)
                    .specialization("Computer Science")
                    .groupNumber("A1")
                    .email("MAT123456@test.com")
                    .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                    .build();
            entityManager.persistAndFlush(student);

            Optional<Student> found = studentRepository.findByMatriculationNumber("MAT123456");

            assertThat(found).isPresent();
            assertThat(found.get().getSpecialization()).isEqualTo("Computer Science");
        }

        @Test
        @DisplayName("Should enforce unique matriculation number constraint")
        void testUniqueMatriculationNumber() {
            Student student1 = Student.builder()
                    .matriculationNumber("UNIQUE001")
                    .academicYear(1)
                    .specialization("CS")
                    .groupNumber("A1")
                    .email("UNIQUE001@test.com")
                    .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                    .build();
            entityManager.persistAndFlush(student1);

            Student student2 = Student.builder()
                    .matriculationNumber("UNIQUE001")
                    .academicYear(2)
                    .specialization("IT")
                    .groupNumber("B1")
                    .email("UNIQUE001_2@test.com")
                    .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                    .build();

            assertThatThrownBy(() -> entityManager.persistAndFlush(student2))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should cascade delete preferences when student is deleted")
        void testCascadeDeletePreferences() {
            Student student = Student.builder()
                    .matriculationNumber("CASCADE001")
                    .academicYear(1)
                    .specialization("CS")
                    .groupNumber("A1")
                    .email("CASCADE001@test.com")
                    .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                    .build();

            OptionalCourse course = OptionalCourse.builder()
                    .code("CASC001")
                    .maxStudents(30)
                    .coursePackage(testPackage)
                    .build();
            course.setName("CASC001 Course");
            entityManager.persist(course);
            entityManager.flush();

            Preference pref = Preference.builder()
                    .priority(1)
                    .student(student)
                    .optionalCourse(course)
                    .build();

            // Initialize preferences list if null, then add preference
            if (student.getPreferences() == null) {
                student.setPreferences(new java.util.ArrayList<>());
            }
            student.getPreferences().add(pref);
            entityManager.persist(student);
            entityManager.flush();

            Long prefId = pref.getId();

            // Delete student
            entityManager.remove(student);
            entityManager.flush();
            entityManager.clear();

            // Preference should be deleted too
            assertThat(preferenceRepository.findById(prefId)).isEmpty();
        }

        @Test
        @DisplayName("Should find students by academic year")
        void testFindByAcademicYear() {
            IntStream.rangeClosed(1, 5).forEach(i -> {
                Student s = Student.builder()
                        .matriculationNumber("YEAR" + i)
                        .academicYear(i % 3 + 1)
                        .specialization("CS")
                        .groupNumber("A1")
                        .email("YEAR" + i + "@test.com")
                        .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                        .build();
                entityManager.persist(s);
            });
            entityManager.flush();

            List<Student> year2Students = studentRepository.findAll().stream()
                    .filter(s -> s.getAcademicYear() == 2)
                    .toList();

            assertThat(year2Students).isNotEmpty();
        }
    }

    // ==================== OPTIONAL COURSE REPOSITORY TESTS ====================

    @Nested
    @DisplayName("OptionalCourseRepository Tests")
    class OptionalCourseRepositoryTests {

        @Test
        @DisplayName("Should find course by code")
        void testFindByCode() {
            OptionalCourse course = OptionalCourse.builder()
                    .code("OPT001")
                    .maxStudents(30)
                    .coursePackage(testPackage)
                    .build();
            course.setName("OPT001 Course");
            entityManager.persistAndFlush(course);

            Optional<OptionalCourse> found = courseRepository.findByCode("OPT001");

            assertThat(found).isPresent();
            assertThat(found.get().getMaxStudents()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should enforce unique code constraint")
        void testUniqueCode() {
            OptionalCourse course1 = OptionalCourse.builder()
                    .code("UNIQUE_CODE")
                    .maxStudents(25)
                    .coursePackage(testPackage)
                    .build();
            course1.setName("UNIQUE_CODE Course");
            entityManager.persistAndFlush(course1);

            OptionalCourse course2 = OptionalCourse.builder()
                    .code("UNIQUE_CODE")
                    .maxStudents(30)
                    .coursePackage(testPackage)
                    .build();
            course2.setName("UNIQUE_CODE Course 2");

            assertThatThrownBy(() -> entityManager.persistAndFlush(course2))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should load enrollments lazily")
        void testLazyLoadEnrollments() {
            OptionalCourse course = OptionalCourse.builder()
                    .code("LAZY001")
                    .maxStudents(30)
                    .coursePackage(testPackage)
                    .build();
            course.setName("LAZY001 Course");
            entityManager.persistAndFlush(course);

            Student student = Student.builder()
                    .matriculationNumber("LAZY_STU001")
                    .academicYear(1)
                    .specialization("CS")
                    .groupNumber("A1")
                    .email("LAZY_STU001@test.com")
                    .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                    .build();
            entityManager.persistAndFlush(student);

            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .optionalCourse(course)
                    .build();
            entityManager.persistAndFlush(enrollment);
            entityManager.clear();

            // Fetch course without triggering lazy load
            OptionalCourse fetchedCourse = courseRepository.findById(course.getId()).orElseThrow();

            // This should trigger lazy load
            assertThat(fetchedCourse.getEnrollments()).hasSize(1);
        }
    }

    // ==================== PREFERENCE REPOSITORY TESTS ====================

    @Nested
    @DisplayName("PreferenceRepository Tests")
    class PreferenceRepositoryTests {

        @Test
        @DisplayName("Should find preferences by student ID ordered by priority")
        void testFindByStudentIdOrderByPriority() {
            Student student = createTestStudent("PREF_STU001");
            OptionalCourse course1 = createTestCourse("PREF_C001");
            OptionalCourse course2 = createTestCourse("PREF_C002");
            OptionalCourse course3 = createTestCourse("PREF_C003");

            // Insert in random order
            entityManager.persist(Preference.builder().priority(3).student(student).optionalCourse(course3).build());
            entityManager.persist(Preference.builder().priority(1).student(student).optionalCourse(course1).build());
            entityManager.persist(Preference.builder().priority(2).student(student).optionalCourse(course2).build());
            entityManager.flush();

            List<Preference> prefs = preferenceRepository.findByStudentIdOrderByPriority(student.getId());

            assertThat(prefs).hasSize(3);
            assertThat(prefs.get(0).getPriority()).isEqualTo(1);
            assertThat(prefs.get(1).getPriority()).isEqualTo(2);
            assertThat(prefs.get(2).getPriority()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should delete all preferences by student ID")
        void testDeleteByStudentId() {
            Student student = createTestStudent("DEL_STU001");
            OptionalCourse course = createTestCourse("DEL_C001");

            entityManager.persist(Preference.builder().priority(1).student(student).optionalCourse(course).build());
            entityManager.persist(Preference.builder().priority(2).student(student).optionalCourse(course).build());
            entityManager.flush();

            assertThat(preferenceRepository.findByStudentIdOrderByPriority(student.getId())).hasSize(2);

            preferenceRepository.deleteByStudentId(student.getId());
            entityManager.flush();
            entityManager.clear();

            assertThat(preferenceRepository.findByStudentIdOrderByPriority(student.getId())).isEmpty();
        }

        @Test
        @DisplayName("Should find preferences by course ID")
        void testFindByOptionalCourseId() {
            OptionalCourse course = createTestCourse("COURSE_FIND001");
            Student student1 = createTestStudent("CF_STU001");
            Student student2 = createTestStudent("CF_STU002");

            entityManager.persist(Preference.builder().priority(1).student(student1).optionalCourse(course).build());
            entityManager.persist(Preference.builder().priority(1).student(student2).optionalCourse(course).build());
            entityManager.flush();

            List<Preference> prefs = preferenceRepository.findByOptionalCourseId(course.getId());

            assertThat(prefs).hasSize(2);
        }
    }

    // ==================== TRANSFER REQUEST REPOSITORY TESTS ====================

    @Nested
    @DisplayName("TransferRequestRepository Tests")
    class TransferRequestRepositoryTests {

        @Test
        @DisplayName("Should find transfer requests by status")
        void testFindByStatus() {
            Student student = createTestStudent("TR_STU001");
            OptionalCourse course1 = createTestCourse("TR_C001");
            OptionalCourse course2 = createTestCourse("TR_C002");

            // Create requests with different statuses
            for (TransferStatus status : TransferStatus.values()) {
                TransferRequest tr = TransferRequest.builder()
                        .student(student)
                        .currentCourse(course1)
                        .requestedCourse(course2)
                        .requestDate(LocalDate.now())
                        .status(status)
                        .build();
                entityManager.persist(tr);
            }
            entityManager.flush();

            List<TransferRequest> pending = transferRequestRepository.findByStatus(TransferStatus.PENDING);
            assertThat(pending).hasSize(1);
            assertThat(pending.get(0).getStatus()).isEqualTo(TransferStatus.PENDING);
        }

        @Test
        @DisplayName("Should find transfer requests by student ID")
        void testFindByStudentId() {
            Student student = createTestStudent("TR_STU002");
            OptionalCourse course1 = createTestCourse("TR_C003");
            OptionalCourse course2 = createTestCourse("TR_C004");

            TransferRequest tr1 = TransferRequest.builder()
                    .student(student)
                    .currentCourse(course1)
                    .requestedCourse(course2)
                    .requestDate(LocalDate.now())
                    .status(TransferStatus.PENDING)
                    .build();
            entityManager.persist(tr1);

            TransferRequest tr2 = TransferRequest.builder()
                    .student(student)
                    .currentCourse(course2)
                    .requestedCourse(course1)
                    .requestDate(LocalDate.now().minusDays(1))
                    .status(TransferStatus.APPROVED)
                    .build();
            entityManager.persist(tr2);
            entityManager.flush();

            List<TransferRequest> requests = transferRequestRepository.findByStudentId(student.getId());

            assertThat(requests).hasSize(2);
        }
    }

    // ==================== ENROLLMENT REPOSITORY TESTS ====================

    @Nested
    @DisplayName("EnrollmentRepository Tests")
    class EnrollmentRepositoryTests {

        @Test
        @DisplayName("Should count enrollments by course")
        void testCountByCourse() {
            OptionalCourse course = createTestCourse("COUNT_C001");

            for (int i = 0; i < 5; i++) {
                Student student = createTestStudent("COUNT_STU" + i);
                Enrollment enrollment = Enrollment.builder()
                        .student(student)
                        .optionalCourse(course)
                        .build();
                entityManager.persist(enrollment);
            }
            entityManager.flush();

            long count = enrollmentRepository.findAll().stream()
                    .filter(e -> e.getOptionalCourse().getId().equals(course.getId()))
                    .count();

            assertThat(count).isEqualTo(5);
        }

        @Test
        @DisplayName("Should find enrollment by student and course")
        void testFindByStudentAndCourse() {
            Student student = createTestStudent("FIND_STU001");
            OptionalCourse course = createTestCourse("FIND_C001");

            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .optionalCourse(course)
                    .build();
            entityManager.persistAndFlush(enrollment);

            List<Enrollment> found = enrollmentRepository.findAll().stream()
                    .filter(e -> e.getStudent().getId().equals(student.getId())
                            && e.getOptionalCourse().getId().equals(course.getId()))
                    .toList();

            assertThat(found).hasSize(1);
        }
    }

    // ==================== HELPER METHODS ====================

    private Student createTestStudent(String matriculationNumber) {
        Student student = Student.builder()
                .matriculationNumber(matriculationNumber)
                .academicYear(1)
                .specialization("CS")
                .groupNumber("A1")
                .email(matriculationNumber + "@test.com")
                .role(ro.uaic.ossp.models.enums.UserRole.STUDENT)
                .build();
        entityManager.persist(student);
        return student;
    }

    private OptionalCourse createTestCourse(String code) {
        OptionalCourse course = OptionalCourse.builder()
                .code(code)
                .maxStudents(30)
                .coursePackage(testPackage)
                .build();
        course.setName(code + " Course");
        entityManager.persist(course);
        return course;
    }
}
