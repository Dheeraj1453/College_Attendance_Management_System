package in.College_Attendance_Management_System.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "students")
@Data // Lombok annotation for getters, setters, toString, equals, and hashCode
public class StudentEntity {

    @Id
    @Column(name = "student_id", unique = true, nullable = false, length = 50)
    private String studentId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "branch", nullable = false, length = 50)
    private String branch;

    @Column(name = "semester", nullable = false)
    private Integer semester;

}
