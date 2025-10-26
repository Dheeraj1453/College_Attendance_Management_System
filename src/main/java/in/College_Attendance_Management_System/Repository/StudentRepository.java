package in.College_Attendance_Management_System.Repository;

import in.College_Attendance_Management_System.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Explicitly mark it as a Repository
public interface StudentRepository extends JpaRepository<StudentEntity, String> {

    /**
     * Custom method required by StudentService to get students for attendance
     */
    List<StudentEntity> findByBranchAndSemesterOrderByFullNameAsc(String branch, Integer semester);

    /**
     * Custom method required by StudentService for searching (e.g., in report)
     */
    List<StudentEntity> findByStudentIdContainingOrFullNameContainingIgnoreCase(String studentIdQuery, String fullNameQuery);

    // findById(String) is inherited from JpaRepository, which uses the ID type 'String'
}
