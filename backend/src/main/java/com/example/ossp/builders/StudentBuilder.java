package com.example.ossp.builders;

import com.example.ossp.entities.Student;

public class StudentBuilder {
    private final Student instance = new Student();

    public static StudentBuilder builder() {
        return new StudentBuilder();
    }

    public StudentBuilder id(Long id) {
        instance.setId(id);
        return this;
    }

    public StudentBuilder firstName(String firstName) {
        instance.setFirstName(firstName);
        return this;
    }

    public StudentBuilder lastName(String lastName) {
        instance.setLastName(lastName);
        return this;
    }

    public StudentBuilder email(String email) {
        instance.setEmail(email);
        return this;
    }

    public StudentBuilder studentNumber(String studentNumber) {
        instance.setStudentNumber(studentNumber);
        return this;
    }

    public Student build() {
        return instance;
    }
}
