package com.example.ossp.obervers;

import com.example.ossp.entities.Student;

/**
 * Concrete Observer for the Observer pattern.
 *
 * StudentNotifier implements the Observer interface and defines
 * how a student should be notified when an event occurs.
 *
 * It receives a reference to a Student object and prints
 * notifications specifically for that student.
 */

public class StudentNotifier implements Observer {
    private Student student;
    public StudentNotifier(Student student) { this.student = student; }
    @Override
    public void update(String message) {
        System.out.println("Notification for " + student.getFirstName() + ": " + message);
    }
}
