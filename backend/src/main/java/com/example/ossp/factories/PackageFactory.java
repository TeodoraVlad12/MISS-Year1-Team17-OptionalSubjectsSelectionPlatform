package com.example.ossp.factories;

import com.example.ossp.entities.CoursePackage;

/**
 * Abstract Factory Pattern for creating CoursePackage objects.
 *
 * The PackageFactory interface defines a method for creating course packages,
 * without specifying the concrete implementation. This allows the client code
 * to use the same interface regardless of whether it is creating a Bachelor
 * or Master package.
 *
 * Benefits:
 * 1. Separates the creation of objects from their concrete classes.
 * 2. Supports creating families of related objects (e.g., Bachelor or Master packages).
 * 3. Makes it easy to extend with new types of packages without changing client code.
 */

public interface PackageFactory {
    CoursePackage createPackage(String name, int year, int semester);
}
