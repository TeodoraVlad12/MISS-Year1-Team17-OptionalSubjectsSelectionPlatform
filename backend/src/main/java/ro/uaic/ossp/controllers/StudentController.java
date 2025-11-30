package ro.uaic.ossp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.uaic.ossp.dtos.StudentAllocationDTO;
import ro.uaic.ossp.repositories.StudentRepository;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    StudentRepository repository;

    @GetMapping("/years")
    public Set<Integer> getStudentYears() {
        return repository.findAllDistinctAcademicYears();
    }

    @GetMapping("/specializations")
    public Set<String> getStudentSpecializations() {
        return repository.findAllDistinctSpecializations();
    }
}