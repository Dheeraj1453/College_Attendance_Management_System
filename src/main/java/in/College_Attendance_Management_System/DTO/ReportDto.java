package in.College_Attendance_Management_System.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for transferring detailed attendance report data to the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private String studentId;
    private String fullName;
    private String branch;
    private Integer semester;
    private Long totalDays;
    private Long presentDays;
    private Double attendancePercentage;
    private List<AttendanceDto> attendanceDetails; // Simple list of dates and presence
}
