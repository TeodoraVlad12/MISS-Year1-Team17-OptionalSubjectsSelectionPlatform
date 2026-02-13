package ro.uaic.ossp.services;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class StudentDataService {

    public List<StudentRecord> loadStudentsFromCsv() {
        List<StudentRecord> students = new ArrayList<>();

        try {
            ClassPathResource resource = new ClassPathResource("studentss.csv");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.trim().isEmpty() || line.startsWith(",,")) {
                    continue; // Skip empty lines
                }

                // Parse CSV line
                String[] parts = line.split(",", -1); // -1 to keep empty strings

                if (parts.length >= 3) {
                    String series = parts[0].trim();
                    String matriculationNumber = parts[1].trim();
                    String name = parts[2].trim();

                    // Check
                    if (!series.isEmpty() && !matriculationNumber.isEmpty() && !name.isEmpty()) {
                        // Generate unique id based on matriculation number
                        long studentId = Math.abs(matriculationNumber.hashCode()) % 1000000L;

                        StudentRecord student = new StudentRecord(
                                studentId,
                                name,
                                matriculationNumber,
                                series,
                                generateRandomGrades() // simulated grades
                        );
                        students.add(student);
                    }
                }
            }

            reader.close();

            // If CSV is empty, create demo data
            if (students.isEmpty()) {
                return createFallbackStudents();
            }

        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            // Fallback: create demo data
            return createFallbackStudents();
        }

        return students;
    }

    private Map<String, Integer> generateRandomGrades() {
        Random random = new Random();
        Map<String, Integer> grades = new HashMap<>();

        // Common mandatory courses for year 1
        String[] subjects = {
                "Programming", "Data Structures", "Databases",
                "Algorithms", "Mathematics", "Computer Architecture"
        };

        for (String subject : subjects) {
            // Generate grades between 6 and 10 (realistic for students)
            grades.put(subject, 6 + random.nextInt(5)); // 6, 7, 8, 9, 10
        }

        return grades;
    }

    private List<StudentRecord> createFallbackStudents() {
        List<StudentRecord> students = new ArrayList<>();

        // Create 10 demo students (with your colleagues' names)
        String[] studentNames = {
                "Bejan M. Paul-Eusebiu", "Bindiu D. Ana-Maria", "Chiriac G.L. Laura-Florina",
                "Tiron A. Raul-Bogdan", "Vlad G. Teodora", "Chiriac D.S. Teodora",
                "Avram N. Tudor-Nicolae", "Biliuţi F.I. Andrei", "Bobu D. Dragoş-Andrei",
                "Căldărescu P. Tudor"
        };

        for (int i = 0; i < studentNames.length; i++) {
            StudentRecord student = new StudentRecord(
                    (long) (i + 1),
                    studentNames[i],
                    "31091001011ENSM25100" + (i + 4),
                    "MISS1",
                    generateRandomGrades()
            );
            students.add(student);
        }

        return students;
    }

    public static class StudentRecord {
        private Long id;
        private String name;
        private String matriculationNumber;
        private String series;
        private Map<String, Integer> grades;
        private Double average;

        public StudentRecord(Long id, String name, String matriculationNumber, String series, Map<String, Integer> grades) {
            this.id = id;
            this.name = name;
            this.matriculationNumber = matriculationNumber;
            this.series = series;
            this.grades = grades;
            this.average = calculateAverage(grades);
        }

        private double calculateAverage(Map<String, Integer> grades) {
            if (grades == null || grades.isEmpty()) {
                return 0.0;
            }
            return grades.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getMatriculationNumber() { return matriculationNumber; }
        public String getSeries() { return series; }
        public Map<String, Integer> getGrades() { return grades; }
        public Double getAverage() { return average; }

        @Override
        public String toString() {
            return String.format("%s (%s) - Average: %.2f", name, series, average);
        }
    }
}