package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "optional_course_requirements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"optional_id", "mandatory_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionalCourseRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optional_id", nullable = false)
    private OptionalCourse optionalCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mandatory_id", nullable = false)
    private MandatoryCourse mandatoryCourse;

    @Column(nullable = false)
    private double percentage; // 0–100
}
