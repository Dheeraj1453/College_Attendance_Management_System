package in.College_Attendance_Management_System.controller;

import in.College_Attendance_Management_System.DTO.AttendanceDto;
import in.College_Attendance_Management_System.DTO.ReportDto;
import in.College_Attendance_Management_System.Entity.AttendanceEntity;
import in.College_Attendance_Management_System.Service.AttendanceService;
import lombok.RequiredArgsConstructor; // New import for DI
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor // Automatically injects final fields via constructor
public class AttendanceController {

    // Dependencies are now final and injected by RequiredArgsConstructor
    private final AttendanceService attendanceService;

    /**
     * POST /api/attendance/mark
     * Saves a batch of attendance records for the current date.
     */
    @PostMapping("/mark")
    public ResponseEntity<?> saveAttendance(@RequestBody List<AttendanceDto> attendanceDtos) {
        try {
            List<AttendanceEntity> savedRecords = attendanceService.saveAttendanceForToday(attendanceDtos);
            return new ResponseEntity<>(savedRecords, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Catches validation errors or student not found errors from the service
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET /api/attendance/report/{studentId}
     * Generates a detailed report for a specific student.
     */
    @GetMapping("/report/{studentId}")
    public ResponseEntity<?> getStudentReport(@PathVariable("studentId") String studentId) {
        try {
            ReportDto report = attendanceService.getStudentReport(studentId);
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // Catches 'Student not found' exception from the service
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
