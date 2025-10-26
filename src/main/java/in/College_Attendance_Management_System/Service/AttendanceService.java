package in.College_Attendance_Management_System.Service;

import in.College_Attendance_Management_System.DTO.AttendanceDto;
import in.College_Attendance_Management_System.DTO.ReportDto;
import in.College_Attendance_Management_System.Entity.AttendanceEntity;
import in.College_Attendance_Management_System.Entity.StudentEntity;
import in.College_Attendance_Management_System.Repository.AttendanceRepository;
import in.College_Attendance_Management_System.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    /**
     * Saves or updates a list of attendance records for the current date.
     */
    @Transactional
    public List<AttendanceEntity> saveAttendanceForToday(List<AttendanceDto> attendanceDtos) {
        LocalDate today = LocalDate.now();

        List<AttendanceEntity> savedRecords = attendanceDtos.stream()
                .map(dto -> {
                    StudentEntity student = studentRepository.findById(dto.getStudentId())
                            .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + dto.getStudentId()));

                    Optional<AttendanceEntity> existingRecord = attendanceRepository.findByStudentAndDate(student, today);

                    AttendanceEntity attendance = existingRecord.orElseGet(AttendanceEntity::new);
                    attendance.setStudent(student);
                    attendance.setDate(today);
                    attendance.setPresent(dto.isPresent());

                    return attendanceRepository.save(attendance);
                })
                .collect(Collectors.toList());

        return savedRecords;
    }

    /**
     * Generates a detailed attendance report for a specific student ID.
     */
    @Transactional(readOnly = true)
    public ReportDto getStudentReport(String studentId) {
        StudentEntity student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        List<AttendanceEntity> records = attendanceRepository.findByStudentOrderByDateDesc(student);

        long totalDays = records.size();
        long presentDays = records.stream().filter(AttendanceEntity::isPresent).count();
        double percentage = totalDays > 0 ? (double) presentDays / totalDays * 100.0 : 0.0;

        List<AttendanceDto> attendanceDetails = records.stream().map(record -> {
            AttendanceDto dto = new AttendanceDto();
            dto.setDate(record.getDate());
            dto.setPresent(record.isPresent());
            dto.setStudentId(student.getStudentId());
            return dto;
        }).collect(Collectors.toList());

        return new ReportDto(
                student.getStudentId(),
                student.getFullName(),
                student.getBranch(),
                student.getSemester(),
                totalDays,
                presentDays,
                Math.round(percentage * 100.0) / 100.0,
                attendanceDetails
        );
    }

    /**
     * 🟢 FIX: NEW METHOD. Deletes all attendance records associated with a student ID.
     * This must be called BEFORE deleting the student to avoid foreign key errors.
     */
    @Transactional
    public void deleteAttendanceByStudentId(String studentId) {
        // Use the new repository method to delete all dependent attendance records
        attendanceRepository.deleteByStudent_StudentId(studentId);
    }
}