package com.example.ossp.obervers;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject class for the Observer pattern.
 *
 * CourseNotifier maintains a list of observers (students or other listeners)
 * and notifies them whenever an event occurs (e.g., course change, transfer request).
 *
 * Methods:
 * - addObserver: register a new observer.
 * - notifyObservers: send a message to all registered observers.
 */

public class CourseNotifier {
    private List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer o) { observers.add(o); }
    public void notifyObservers(String message) { observers.forEach(o -> o.update(message)); }
}