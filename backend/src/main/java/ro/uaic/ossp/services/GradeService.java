package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ro.uaic.ossp.dtos.GradeUploadResultDTO;
import ro.uaic.ossp.models.CourseBase;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.GradeEntry;
import ro.uaic.ossp.models.OptionalCourse;
import ro.uaic.ossp.repositories.GradeRepository;
import ro.uaic.ossp.repositories.MandatoryCourseRepository;
import ro.uaic.ossp.repositories.OptionalCourseRepository;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.security.exceptions.NotFoundException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Service for CSV upload. CSV format:
 * header: studentName,matricol,Course A,Course B,...
 * rows: "Ion Popescu",12345,9.5,8.0,
 */
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepo;
    private final MandatoryCourseRepository mandatoryRepo;
    private final OptionalCourseRepository optionalRepo;

    @Transactional
    public GradeUploadResultDTO uploadCsv(MultipartFile file, boolean overwriteIfExists) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("CSV file is empty");
        }

        int totalRows = 0, inserted = 0, updated = 0, skipped = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = br.readLine();
            if (headerLine == null) throw new BadRequestException("CSV header missing");

            String[] headers = splitCsvLine(headerLine);
            if (headers.length < 3) throw new BadRequestException("CSV must have: name, matricol, course1, course2...");

            List<String> courseNames = new ArrayList<>();
            for (int i = 2; i < headers.length; i++) {
                courseNames.add(headers[i].trim());
            }

            // Preload all courses
            Map<String, CourseBase> courseMap = new HashMap<>();
            for (String cn : courseNames) {
                CourseBase c = findCourseByName(cn);
                if (c == null) {
                    errors.add("Course not found: " + cn);
                } else {
                    courseMap.put(cn.toLowerCase(), c);
                }
            }

            if (!errors.isEmpty()) {
                throw new NotFoundException("Upload aborted: " + String.join("; ", errors));
            }

            String line;
            while ((line = br.readLine()) != null) {
                totalRows++;

                String[] cols = splitCsvLine(line);
                if (cols.length < 2) {
                    skipped++;
                    errors.add("Invalid row: " + (totalRows + 1));
                    continue;
                }

                String studentName = cols[0].trim();
                String matricol = cols[1].trim();

                if (matricol.isEmpty()) {
                    skipped++;
                    continue;
                }

                Optional<Grade> existingOpt = gradeRepo.findByMatricol(matricol);
                Grade grade;

                if (existingOpt.isPresent()) {
                    if (!overwriteIfExists) {
                        skipped++;
                        continue;
                    }
                    grade = existingOpt.get();
                    grade.clearEntries();
                    grade.setStudentName(studentName);
                } else {
                    grade = Grade.builder()
                            .matricol(matricol)
                            .studentName(studentName)
                            .build();
                }

                // Parse grade values
                for (int i = 2; i < headers.length; i++) {

                    String courseName = courseNames.get(i - 2);
                    String raw = (i < cols.length) ? cols[i].trim() : "";

                    if (raw.isEmpty()) continue;

                    double value;
                    try {
                        value = Double.parseDouble(raw.replace(",", "."));
                    } catch (Exception e) {
                        errors.add("Invalid grade '" + raw + "' for course " + courseName);
                        continue;
                    }

                    CourseBase course = courseMap.get(courseName.toLowerCase());
                    GradeEntry entry = new GradeEntry();
                    entry.setCourse(course);
                    entry.setGradeValue(value);

                    grade.addEntry(entry);
                }

                gradeRepo.save(grade);
                if (existingOpt.isPresent()) updated++; else inserted++;
            }

        } catch (Exception e) {
            throw new BadRequestException("Error processing CSV: " + e.getMessage());
        }

        String summary = "Inserted=" + inserted + ", Updated=" + updated + ", Skipped=" + skipped;
        return new GradeUploadResultDTO(totalRows, inserted, updated, skipped, summary);
    }

    private CourseBase findCourseByName(String name) {
        return optionalRepo.findByNameIgnoreCase(name)
                .map(c -> (CourseBase) c)
                .orElseGet(() -> mandatoryRepo.findByNameIgnoreCase(name)
                        .map(c -> (CourseBase) c)
                        .orElse(null));
    }

    public String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') { inQuotes = !inQuotes; continue; }
            if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}