package in.College_Attendance_Management_System.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;


@Entity
@Table(name = "attendance_records",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "attendance_date"})
        }
)
@Data
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",            // The column in the 'attendance_records' table
            referencedColumnName = "student_id", // Tells JPA the name of the PK column in 'students' table
            nullable = false
    )
    private StudentEntity student;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate date;

    @Column(name = "is_present", nullable = false)
    private boolean isPresent;

    // Set the date to today before persisting if it's not already set
    @PrePersist
    private void onPrePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }
}
