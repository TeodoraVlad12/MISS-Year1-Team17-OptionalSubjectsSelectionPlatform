package com.example.ossp.factories;

import com.example.ossp.entities.CoursePackage;

/**
 * Concrete Factory for creating Bachelor course packages.
 */

public class BachelorFactory implements PackageFactory {
    public CoursePackage createPackage(String name, int year, int semester) {
        CoursePackage pkg = new CoursePackage();
        pkg.setName(name);
        pkg.setYear(year);
        pkg.setSemester(semester);
        pkg.setLevel("Bachelor");
        return pkg;
    }
}
