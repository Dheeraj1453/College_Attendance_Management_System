package in.College_Attendance_Management_System.controller;


import in.College_Attendance_Management_System.DTO.StudentDto;
import in.College_Attendance_Management_System.Entity.StudentEntity;
import in.College_Attendance_Management_System.Service.StudentService;
import lombok.RequiredArgsConstructor; // New import for DI
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor // Automatically injects final fields via constructor
public class StudentController {

    // Dependency is now final and injected by RequiredArgsConstructor
    private final StudentService studentService;

    /**
     * POST /api/students
     * Adds a new student.
     */
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody StudentDto studentDto) {
        try {
            StudentEntity savedStudent = studentService.addStudent(studentDto);
            return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /api/students/{id}
     * Retrieves a student by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentEntity> getStudentById(@PathVariable("id") String id) {
        return studentService.getStudentById(id)
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * PUT /api/students/{id}
     * Updates an existing student by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable("id") String id, @RequestBody StudentDto studentDto) {
        try {
            StudentEntity updatedStudent = studentService.updateStudent(id, studentDto);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Catches 'Student not found' exception
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /api/students/{id}
     * Deletes a student by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") String id) {
        try {
            studentService.deleteStudent(id);
            return new ResponseEntity<>("Student deleted successfully.", HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * GET /api/students/filter?branch=...&semester=...
     * Lists students by branch and semester for attendance marking.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<StudentEntity>> getStudentsForAttendance(
            @RequestParam("branch") String branch,
            @RequestParam("semester") Integer semester) {
        List<StudentEntity> students = studentService.getStudentsForAttendance(branch, semester);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
}
