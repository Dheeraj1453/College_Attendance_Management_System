package in.College_Attendance_Management_System.Repository;

import in.College_Attendance_Management_System.Entity.AttendanceEntity;
import in.College_Attendance_Management_System.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Import for the delete operation

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    /**
     * Finds attendance records for a specific student, ordered by date.
     * Required by AttendanceService.getStudentReport.
     */
    List<AttendanceEntity> findByStudentOrderByDateDesc(StudentEntity student);

    /**
     * Finds a specific attendance record for a student on a specific date.
     * Required by AttendanceService.saveAttendanceForToday for update logic.
     * Optional is standard here as the record might not exist.
     */
    Optional<AttendanceEntity> findByStudentAndDate(StudentEntity student, LocalDate date);

    /**
     * 🟢 FIX: NEW METHOD. Deletes all attendance records associated with a student ID.
     * The naming convention relies on the 'student' field in AttendanceEntity having a 'studentId' property.
     * This is crucial for fixing the Foreign Key constraint during student deletion.
     */
    @Transactional
    void deleteByStudent_StudentId(String studentId);
}