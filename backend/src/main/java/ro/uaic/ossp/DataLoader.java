package ro.uaic.ossp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ro.uaic.ossp.models.CoursePackage;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.models.Preference;
import ro.uaic.ossp.models.Student;
import ro.uaic.ossp.repositories.CoursePackageRepository;
import ro.uaic.ossp.repositories.GradeRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;
import ro.uaic.ossp.repositories.PreferenceRepository;
import ro.uaic.ossp.repositories.StudentRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final PreferenceRepository preferenceRepository;
    private final OptionalCourseRepository optionalCourseRepository;
    private final CoursePackageRepository coursePackageRepository;

    public DataLoader(
            StudentRepository studentRepository,
            GradeRepository gradeRepository,
            PreferenceRepository preferenceRepository,
            OptionalCourseRepository optionalCourseRepository,
            CoursePackageRepository coursePackageRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.preferenceRepository = preferenceRepository;
        this.optionalCourseRepository = optionalCourseRepository;
        this.coursePackageRepository = coursePackageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Student> savedStudents;

        if (studentRepository.count() == 0) {
            List<Student> toSave = List.of(
                    createStudent("Ana", "Pop", "M2025001", 1, "CS"),
                    createStudent("Ion", "Creanga", "M2025002", 1, "CS"),
                    createStudent("Bob", "Thebuilder", "M2025003", 1, "CS")
            );
            savedStudents = studentRepository.saveAll(toSave);
        } else {
            savedStudents = studentRepository.findAll();
        }

        // seed grades
        if (gradeRepository.count() == 0 && !savedStudents.isEmpty()) {
            List<Grade> mock = new ArrayList<>();
            int limit = Math.min(savedStudents.size(), 3);
            for (int i = 0; i < limit; i++) {
                Student s = savedStudents.get(i);
                mock.add(createGrade(s, 2025, "CS", "Java", 9.5 - i * 0.5));
                mock.add(createGrade(s, 2025, "CS", "Software Engineering", 8.7 - i * 0.5));
            }
            gradeRepository.saveAll(mock);
        }

        // ensure at least 7 OptionalCourse entries exist
        int requiredOptionals = 7;
        if (optionalCourseRepository.count() < requiredOptionals) {
            // ensure a CoursePackage exists
            CoursePackage defaultPkg = coursePackageRepository.findAll().stream().findFirst().orElseGet(() -> {
                CoursePackage p = new CoursePackage();
                p.setName("Default package");
                p.setDescription("Auto-created package for DataLoader");
                p.setYear(2025);
                p.setSemester(1);
                p.setLevel("Master");
                return coursePackageRepository.save(p);
            });

            List<OptionalCourse> created = new ArrayList<>();
            int toCreate = requiredOptionals - (int) optionalCourseRepository.count();
            for (int i = 1; i <= toCreate; i++) {
                OptionalCourse oc = new OptionalCourse();
                String name = "Optional " + i;
                invokeStringSetterIfExists(oc, "setName", name);
                invokeStringSetterIfExists(oc, "setCode", "OPT_" + i);
                // set the required CoursePackage relation (try direct setter or fallback reflection)
                invokeObjectSetterIfExists(oc, "setCoursePackage", defaultPkg);
                invokeObjectSetterIfExists(oc, "setPackage", defaultPkg); // try alternative setter names
                created.add(oc);
            }
            optionalCourseRepository.saveAll(created);
            System.out.println("DataLoader: created " + created.size() + " OptionalCourse entries (to reach " + requiredOptionals + ").");
        }

        // seed 7 preferences per student (priority 1..7)
        if (preferenceRepository.count() == 0 && !savedStudents.isEmpty()) {
            List<OptionalCourse> optionals = optionalCourseRepository.findAll();
            if (optionals.isEmpty()) {
                System.out.println("DataLoader: no OptionalCourse entries found; cannot seed Preferences.");
            } else {
                List<Preference> prefs = new ArrayList<>();
                for (Student s : savedStudents) {
                    for (int rank = 0; rank < requiredOptionals; rank++) {
                        Preference p = new Preference();
                        p.setPriority(rank + 1);
                        p.setStudent(s);
                        // if fewer optionals than required, reuse them cyclically
                        OptionalCourse chosen = optionals.get(rank % optionals.size());
                        p.setOptionalCourse(chosen);
                        prefs.add(p);
                    }
                }
                preferenceRepository.saveAll(prefs);
                System.out.println("DataLoader: seeded " + prefs.size() + " preferences (" + requiredOptionals + " each) for " + savedStudents.size() + " students.");
            }
        }

        // helpful logging
        System.out.println("DataLoader: created/loaded students:");
        for (Student s : savedStudents) {
            try {
                System.out.printf(" - id=%d, %s %s, matriculation=%s, specialization=%s%n",
                        s.getId(), s.getFirstName(), s.getLastName(), s.getMatriculationNumber(), s.getSpecialization());
            } catch (Exception ignored) {}
        }
    }

    private void invokeStringSetterIfExists(Object obj, String methodName, String value) {
        try {
            Method m = obj.getClass().getMethod(methodName, String.class);
            m.invoke(obj, value);
            return;
        } catch (ReflectiveOperationException ignored) {
            // method not found or cannot be invoked; fall through to fallback search
        }
        for (Method mm : obj.getClass().getMethods()) {
            if (!mm.getName().toLowerCase().contains(methodName.replace("set", "").toLowerCase())) continue;
            if (mm.getParameterCount() == 1 && mm.getParameterTypes()[0].equals(String.class)) {
                try {
                    mm.invoke(obj, value);
                    return;
                } catch (Exception ignored) {}
            }
        }
    }

    private void invokeObjectSetterIfExists(Object obj, String methodName, Object value) {
        if (value == null) return;
        try {
            Method m = obj.getClass().getMethod(methodName, value.getClass());
            m.invoke(obj, value);
            return;
        } catch (ReflectiveOperationException ignored) {
            // fall through
        }
        for (Method mm : obj.getClass().getMethods()) {
            if (!mm.getName().toLowerCase().contains(methodName.replace("set", "").toLowerCase())) continue;
            if (mm.getParameterCount() == 1) {
                Class<?> param = mm.getParameterTypes()[0];
                if (param.isAssignableFrom(value.getClass())) {
                    try {
                        mm.invoke(obj, value);
                        return;
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    private Student createStudent(String firstName, String lastName, String matriculation, Integer academicYear, String specialization) {
        Student s = new Student();
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setMatriculationNumber(matriculation);
        s.setAcademicYear(academicYear);
        s.setSpecialization(specialization);

        // ensure email is not null
        String email = (firstName + "." + lastName + "." + matriculation + "@example.com").toLowerCase();
        try {
            s.getClass().getMethod("setEmail", String.class).invoke(s, email);
        } catch (Exception ignored) {}

        // ensure role is not null
        try {
            s.getClass().getMethod("setRole", String.class).invoke(s, "STUDENT");
        } catch (NoSuchMethodException e) {
            for (var m : s.getClass().getMethods()) {
                if (!m.getName().equals("setRole") || m.getParameterCount() != 1) continue;
                Class<?> p = m.getParameterTypes()[0];
                try {
                    if (p.isEnum()) {
                        @SuppressWarnings("unchecked")
                        Class<? extends Enum> enumType = (Class<? extends Enum>) p;
                        Object enumVal = Enum.valueOf(enumType, "STUDENT");
                        m.invoke(s, enumVal);
                    } else if (p.equals(String.class)) {
                        m.invoke(s, "STUDENT");
                    }
                    break;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        return s;
    }

    private Grade createGrade(Student student, Integer year, String spec, String subject, Double value) {
        Grade g = new Grade();
        g.setStudent(student);
        g.setYear(year);
        g.setSpecialization(spec);
        g.setSubject(subject);
        g.setValue(value);
        return g;
    }
}