package in.College_Attendance_Management_System.DTO;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

/**
 * Wrapper class to help Spring MVC bind a list of objects from a form submission.
 * This is used for the "Mark Attendance" form.
 */
@Data
public class AttendanceFormWrapper {

    private List<AttendanceDto> attendanceList = new ArrayList<>();

    // Lombok will generate getAttendanceList() and setAttendanceList()
}
