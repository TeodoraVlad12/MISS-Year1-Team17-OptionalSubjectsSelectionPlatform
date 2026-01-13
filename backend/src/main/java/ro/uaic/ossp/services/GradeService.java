package ro.uaic.ossp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ro.uaic.ossp.dtos.GradeUploadResultDTO;
import ro.uaic.ossp.models.CourseBase;
import ro.uaic.ossp.models.Grade;
import ro.uaic.ossp.models.GradeEntry;
import ro.uaic.ossp.repositories.GradeRepository;
import ro.uaic.ossp.security.exceptions.BadRequestException;
import ro.uaic.ossp.security.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Service for CSV upload. CSV format:
 * header: studentName,matricol,Course A,Course B,...
 * rows: "Ion Popescu",12345,9.5,8.0,
 */
@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepo;
    private final CourseService courseService; // NEW: Extract course logic
    private final CsvParser csvParser; // NEW: Extract CSV parsing

    @Transactional
    public GradeUploadResultDTO uploadCsv(MultipartFile file, boolean overwriteIfExists) {
        validateFile(file);

        CsvData csvData = csvParser.parse(file); // EXTRACT CLASS
        validateCsvHeaders(csvData.getHeaders());

        Map<String, CourseBase> courseMap = courseService.loadCourses(csvData.getCourseNames()); // EXTRACT METHOD
        validateCourses(courseMap, csvData.getCourseNames());

        UploadStatistics stats = processCsvRows(csvData, courseMap, overwriteIfExists); // EXTRACT METHOD

        return buildUploadResult(stats); // EXTRACT METHOD
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("CSV file is empty");
        }
    }

    private void validateCsvHeaders(String[] headers) {
        if (headers.length < 3) {
            throw new BadRequestException("CSV must have: name, matricol, course1, course2...");
        }
    }

    private void validateCourses(Map<String, CourseBase> courseMap, List<String> courseNames) {
        List<String> missingCourses = courseNames.stream()
                .filter(cn -> !courseMap.containsKey(cn.toLowerCase()))
                .toList();

        if (!missingCourses.isEmpty()) {
            throw new NotFoundException("Courses not found: " + String.join(", ", missingCourses));
        }
    }

    private UploadStatistics processCsvRows(CsvData csvData, Map<String, CourseBase> courseMap,
                                            boolean overwriteIfExists) {
        UploadStatistics stats = new UploadStatistics();

        for (CsvRow row : csvData.getRows()) { // EXTRACT CLASS for CsvRow
            processRow(row, courseMap, overwriteIfExists, stats);
        }

        return stats;
    }

    private void processRow(CsvRow row, Map<String, CourseBase> courseMap,
                            boolean overwriteIfExists, UploadStatistics stats) {
        stats.incrementTotalRows();

        if (row.getMatricol().isEmpty()) {
            stats.incrementSkipped();
            return;
        }

        try {
            Grade grade = findOrCreateGrade(row, overwriteIfExists);
            processGradeEntries(grade, row, courseMap);
            saveGrade(grade, stats);
        } catch (Exception e) {
            stats.addError("Row " + stats.getTotalRows() + ": " + e.getMessage());
            stats.incrementSkipped();
        }
    }

    private Grade findOrCreateGrade(CsvRow row, boolean overwriteIfExists) {
        return gradeRepo.findByMatricol(row.getMatricol())
                .map(existing -> handleExistingGrade(existing, row, overwriteIfExists))
                .orElse(createNewGrade(row));
    }

    private Grade handleExistingGrade(Grade existing, CsvRow row, boolean overwriteIfExists) {
        if (!overwriteIfExists) {
            throw new SkipRowException("Grade already exists and overwrite is false");
        }
        existing.clearEntries();
        existing.setStudentName(row.getStudentName());
        return existing;
    }

    private Grade createNewGrade(CsvRow row) {
        return Grade.builder()
                .matricol(row.getMatricol())
                .studentName(row.getStudentName())
                .build();
    }

    private void processGradeEntries(Grade grade, CsvRow row, Map<String, CourseBase> courseMap) {
        for (GradeEntryData entryData : row.getGradeEntries()) { // EXTRACT CLASS
            CourseBase course = courseMap.get(entryData.getCourseName().toLowerCase());
            if (course != null && entryData.hasGradeValue()) {
                GradeEntry entry = createGradeEntry(course, entryData.getGradeValue());
                grade.addEntry(entry);
            }
        }
    }

    private GradeEntry createGradeEntry(CourseBase course, double gradeValue) {
        GradeEntry entry = new GradeEntry();
        entry.setCourse(course);
        entry.setGradeValue(gradeValue);
        return entry;
    }

    private void saveGrade(Grade grade, UploadStatistics stats) {
        gradeRepo.save(grade);
        if (grade.getId() == null) {
            stats.incrementInserted();
        } else {
            stats.incrementUpdated();
        }
    }

    private GradeUploadResultDTO buildUploadResult(UploadStatistics stats) {
        String summary = String.format("Inserted=%d, Updated=%d, Skipped=%d",
                stats.getInserted(), stats.getUpdated(), stats.getSkipped());
        return new GradeUploadResultDTO(
                stats.getTotalRows(),
                stats.getInserted(),
                stats.getUpdated(),
                stats.getSkipped(),
                summary);
    }
}
