package ro.uaic.ossp.services;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CsvParser {
    public CsvData parse(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new CsvData(new String[0], List.of(), List.of());
            }
            String[] headers = splitCsvLine(headerLine);
            // headers: name, matricol, course1, course2...
            List<String> courseNames = new ArrayList<>();
            if (headers.length > 2) {
                for (int i = 2; i < headers.length; i++) {
                    courseNames.add(headers[i].trim());
                }
            }

            List<CsvRow> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = splitCsvLine(line);
                String studentName = parts.length > 0 ? trimQuotes(parts[0].trim()) : "";
                String matricol = parts.length > 1 ? parts[1].trim() : "";
                List<GradeEntryData> gradeEntries = new ArrayList<>();
                for (int i = 2; i < headers.length; i++) {
                    String course = headers[i].trim();
                    Double value = null;
                    if (parts.length > i && !parts[i].trim().isEmpty()) {
                        try {
                            value = Double.parseDouble(parts[i].trim());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    gradeEntries.add(new GradeEntryData(course, value));
                }
                rows.add(new CsvRow(studentName, matricol, gradeEntries));
            }

            return new CsvData(headers, rows, courseNames);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV: " + e.getMessage(), e);
        }
    }

    private String[] splitCsvLine(String line) {
        // Very simple CSV splitter that handles quoted commas in the first column (name)
        List<String> parts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            if (c == ',' && !inQuotes) {
                parts.add(cur.toString());
                cur.setLength(0);
                continue;
            }
            cur.append(c);
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    private String trimQuotes(String s) {
        if (s == null) return null;
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}

