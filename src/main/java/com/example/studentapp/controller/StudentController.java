package com.example.studentapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {

    // Simple student data model
    public record Student(int id, String name, String department) {}

    // Sample student records
    private final List<Student> students = List.of(
        new Student(1, "Robin", "Computer Science"),
        new Student(2, "Priya", "Electronics"),
        new Student(3, "Rahul", "Information Technology")
    );

    // Application information endpoint
    @GetMapping("/message")
    public String getMessage() {
        return "Student Management Application is Running!";
    }

    // Retrieve all students
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return students;
    }

    // Retrieve a student using their ID
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(
            @PathVariable int id) {

        return students.stream()
                .filter(student -> student.id() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
