package in.College_Attendance_Management_System.DTO;

import in.College_Attendance_Management_System.Entity.StudentEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving student data from the frontend (Add/Update).
 */
@Data
@NoArgsConstructor
public class StudentDto {
    private String studentId;
    private String fullName;
    private String gender;
    private String branch;
    private Integer semester;

    /**
     * Constructor to map StudentEntity to StudentDto, needed for Update page loading.
     */
    public StudentDto(StudentEntity entity) {
        this.studentId = entity.getStudentId();
        this.fullName = entity.getFullName();
        this.gender = entity.getGender();
        this.branch = entity.getBranch();
        this.semester = entity.getSemester();
    }
}