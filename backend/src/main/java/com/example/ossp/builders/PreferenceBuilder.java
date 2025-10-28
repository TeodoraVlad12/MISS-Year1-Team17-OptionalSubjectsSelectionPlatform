package com.example.ossp.builders;

import com.example.ossp.entities.OptionalCourse;
import com.example.ossp.entities.Preference;
import com.example.ossp.entities.Student;

public class PreferenceBuilder {
    private final Preference instance = new Preference();

    public static PreferenceBuilder builder() {
        return new PreferenceBuilder();
    }

    public PreferenceBuilder id(Long id) {
        instance.setId(id);
        return this;
    }

    public PreferenceBuilder priority(int priority) {
        instance.setPriority(priority);
        return this;
    }

    public PreferenceBuilder student(Student student) {
        instance.setStudent(student);
        return this;
    }

    public PreferenceBuilder optionalCourse(OptionalCourse optionalCourse) {
        instance.setOptionalCourse(optionalCourse);
        return this;
    }

    public Preference build() {
        return instance;
    }
}
