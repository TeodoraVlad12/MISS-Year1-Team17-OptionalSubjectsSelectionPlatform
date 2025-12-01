package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grades")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long id;

    @Column(name = "matricol", nullable = false)
    private String matricol;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    // One grade per course (GradeEntry)
    @OneToMany(mappedBy = "grade", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GradeEntry> entries = new ArrayList<>();

    public void addEntry(GradeEntry entry) {
        entry.setGrade(this);
        this.entries.add(entry);
    }

    public void clearEntries() {
        this.entries.forEach(e -> e.setGrade(null));
        this.entries.clear();
    }
}
