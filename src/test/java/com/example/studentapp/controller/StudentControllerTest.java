
package com.example.studentapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentControllerTest {

    private StudentController controller;

    @BeforeEach
    void setUp() {
        controller = new StudentController();
    }

    // Test 1: Application message
    @Test
    void testGetMessage() {

        String message = controller.getMessage();

        assertEquals(
            "Student Management Application is Running!",
            message
        );
    }

    // Test 2: Retrieve all students
    @Test
    void testGetAllStudents() {

        List<StudentController.Student> students =
                controller.getAllStudents();

        assertEquals(3, students.size());
    }

    // Test 3: Retrieve an existing student
    @Test
    void testGetStudentById() {

        ResponseEntity<StudentController.Student> response =
                controller.getStudentById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());

        assertEquals("Arun", response.getBody().name());
    }

    // Test 4: Retrieve a non-existing student
    @Test
    void testGetInvalidStudentId() {

        ResponseEntity<StudentController.Student> response =
                controller.getStudentById(99);

        assertEquals(
            HttpStatus.NOT_FOUND,
            response.getStatusCode()
        );

        assertNull(response.getBody());
    }
}
