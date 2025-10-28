package com.example.ossp.builders;

import com.example.ossp.entities.CoursePackage;
import com.example.ossp.entities.OptionalCourse;

public class OptionalCourseBuilder {
    private final OptionalCourse instance = new OptionalCourse();

    public static OptionalCourseBuilder builder() {
        return new OptionalCourseBuilder();
    }

    public OptionalCourseBuilder id(Long id) {
        instance.setId(id);
        return this;
    }

    public OptionalCourseBuilder name(String name) {
        instance.setName(name);
        return this;
    }

    public OptionalCourseBuilder code(String code) {
        instance.setCode(code);
        return this;
    }

    public OptionalCourseBuilder maxStudents(int maxStudents) {
        instance.setMaxStudents(maxStudents);
        return this;
    }

    public OptionalCourseBuilder coursePackage(CoursePackage coursePackage) {
        instance.setCoursePackage(coursePackage);
        return this;
    }

    public OptionalCourse build() {
        return instance;
    }
}
