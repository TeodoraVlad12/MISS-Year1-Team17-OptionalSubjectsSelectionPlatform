package ro.uaic.ossp.dtos;

import java.util.List;
import java.util.Map;

public class StudentResponseDTO {

    private Long id;
    private String fullName;
    private List<String> preferences;
    private Map<String, Double> grades;

    public StudentResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    // This is the missing setter your service expects
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<String> preferences) {
        this.preferences = preferences;
    }

    public Map<String, Double> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, Double> grades) {
        this.grades = grades;
    }
}