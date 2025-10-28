package com.example.ossp.builders;

import com.example.ossp.entities.Enrollment;
import com.example.ossp.entities.OptionalCourse;
import com.example.ossp.entities.Student;

public class EnrollmentBuilder {
    private final Enrollment instance = new Enrollment();

    public static EnrollmentBuilder builder() {
        return new EnrollmentBuilder();
    }

    public EnrollmentBuilder id(Long id) {
        instance.setId(id);
        return this;
    }

    public EnrollmentBuilder student(Student student) {
        instance.setStudent(student);
        return this;
    }

    public EnrollmentBuilder optionalCourse(OptionalCourse optionalCourse) {
        instance.setOptionalCourse(optionalCourse);
        return this;
    }

    public Enrollment build() {
        return instance;
    }
}
