package com.example.ossp.obervers;


/**
 * Observer interface for the Observer design pattern.
 *
 * Any class that wants to receive notifications from a subject
 * (e.g., CourseNotifier) should implement this interface and
 * define the update() method.
 */

public interface Observer { void update(String message); }
