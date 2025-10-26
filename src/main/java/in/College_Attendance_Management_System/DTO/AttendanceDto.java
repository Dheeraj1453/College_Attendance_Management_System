package in.College_Attendance_Management_System.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO for transferring attendance status back to the frontend.
 */
@Data
@NoArgsConstructor
public class AttendanceDto {
    private String studentId;
    private String fullName;
    private String branch;
    private Integer semester;
    private boolean isPresent;
    private LocalDate date;
}
