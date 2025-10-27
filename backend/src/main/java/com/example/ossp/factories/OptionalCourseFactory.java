package com.example.ossp.factories;

import com.example.ossp.entities.OptionalCourse;

public class OptionalCourseFactory {

    /**
     * Factory Method for creating OptionalCourse objects.
     * This method encapsulates the creation logic, so the client code
     * does not need to call 'new' directly or set properties manually.
     *
     * Benefits:
     * 1. Centralizes object creation logic.
     * 2. Hides the details of object instantiation from the client.
     * 3. Makes it easier to extend in the future (e.g., creating subclasses of OptionalCourse).
     */
    public OptionalCourse create(String name, String code, int maxStudents) {
        OptionalCourse course = new OptionalCourse();
        course.setName(name);
        course.setCode(code);
        course.setMaxStudents(maxStudents);
        return course;
    }
}