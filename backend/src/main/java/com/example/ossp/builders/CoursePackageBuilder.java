package com.example.ossp.builders;

import com.example.ossp.entities.CoursePackage;

/**
 * Builder Pattern for creating CoursePackage objects step by step.
 *
 * This class allows constructing a CoursePackage instance in a controlled and readable way,
 * especially useful when the object has multiple attributes.
 *
 * Benefits:
 * 1. Provides a fluent interface for setting properties of CoursePackage.
 * 2. Separates the construction of a complex object from its representation.
 * 3. Makes it easy to create different variations of CoursePackage without having multiple constructors.
 *
 * Example usage:
 * CoursePackage pkg = new CoursePackageBuilder()
 *                          .setName("Math Package")
 *                          .setYear(1)
 *                          .setSemester(1)
 *                          .setLevel("Bachelor")
 *                          .build();
 */

public class CoursePackageBuilder {
    private CoursePackage pkg = new CoursePackage();

    public CoursePackageBuilder setName(String name) { pkg.setName(name); return this; }
    public CoursePackageBuilder setYear(int year) { pkg.setYear(year); return this; }
    public CoursePackageBuilder setSemester(int semester) { pkg.setSemester(semester); return this; }
    public CoursePackageBuilder setLevel(String level) { pkg.setLevel(level); return this; }

    public CoursePackage build() { return pkg; }
}
