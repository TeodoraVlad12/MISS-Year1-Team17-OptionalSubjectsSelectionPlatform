package ro.uaic.ossp.models;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "course_type")
public abstract class CourseBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String name;
}
