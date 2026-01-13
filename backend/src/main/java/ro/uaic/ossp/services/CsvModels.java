package ro.uaic.ossp.services;

import ro.uaic.ossp.security.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Data holder for parsed CSV
class CsvData {
    private final String[] headers;
    private final List<CsvRow> rows;
    private final List<String> courseNames;

    public CsvData(String[] headers, List<CsvRow> rows, List<String> courseNames) {
        this.headers = headers != null ? headers : new String[0];
        this.rows = rows != null ? rows : Collections.emptyList();
        this.courseNames = courseNames != null ? courseNames : Collections.emptyList();
    }

    public String[] getHeaders() {
        return headers;
    }

    public List<CsvRow> getRows() {
        return rows;
    }

    public List<String> getCourseNames() {
        return courseNames;
    }
}

class CsvRow {
    private final String studentName;
    private final String matricol;
    private final List<GradeEntryData> gradeEntries;

    public CsvRow(String studentName, String matricol, List<GradeEntryData> gradeEntries) {
        this.studentName = studentName != null ? studentName : "";
        this.matricol = matricol != null ? matricol : "";
        this.gradeEntries = gradeEntries != null ? gradeEntries : new ArrayList<>();
    }

    public String getStudentName() {
        return studentName;
    }

    public String getMatricol() {
        return matricol;
    }

    public List<GradeEntryData> getGradeEntries() {
        return gradeEntries;
    }
}

class GradeEntryData {
    private final String courseName;
    private final Double gradeValue;

    public GradeEntryData(String courseName, Double gradeValue) {
        this.courseName = courseName != null ? courseName : "";
        this.gradeValue = gradeValue;
    }

    public String getCourseName() {
        return courseName;
    }

    public Double getGradeValue() {
        return gradeValue;
    }

    public boolean hasGradeValue() {
        return gradeValue != null;
    }
}

class UploadStatistics {
    private int totalRows = 0;
    private int inserted = 0;
    private int updated = 0;
    private int skipped = 0;
    private final List<String> errors = new ArrayList<>();

    public void incrementTotalRows() { this.totalRows++; }
    public void incrementInserted() { this.inserted++; }
    public void incrementUpdated() { this.updated++; }
    public void incrementSkipped() { this.skipped++; }
    public void addError(String err) { this.errors.add(err); }

    public int getTotalRows() { return totalRows; }
    public int getInserted() { return inserted; }
    public int getUpdated() { return updated; }
    public int getSkipped() { return skipped; }
    public List<String> getErrors() { return errors; }
}

// Simple runtime exception used to signal skipping a CSV row
class SkipRowException extends RuntimeException {
    public SkipRowException(String message) { super(message); }
}
