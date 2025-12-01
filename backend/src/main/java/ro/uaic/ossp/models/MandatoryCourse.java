package ro.uaic.ossp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mandatory_courses")
@DiscriminatorValue("MANDATORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MandatoryCourse extends CourseBase {

    @Column(nullable = false, unique = true)
    private String code;   // opțional dar recomandat
}
