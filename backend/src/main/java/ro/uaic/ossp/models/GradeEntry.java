package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grade_entries")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GradeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_entry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandatory_course_id")
    private MandatoryCourse mandatoryCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optional_course_id")
    private OptionalCourse optionalCourse;

    @Column(name = "grade_value")
    private Double gradeValue;

    /** Setter unificat */
    public void setCourse(CourseBase course) {
        if (course instanceof MandatoryCourse m) {
            this.mandatoryCourse = m;
            this.optionalCourse = null;
        } else if (course instanceof OptionalCourse o) {
            this.optionalCourse = o;
            this.mandatoryCourse = null;
        }
    }
}
