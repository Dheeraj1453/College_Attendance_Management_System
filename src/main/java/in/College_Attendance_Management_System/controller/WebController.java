package in.College_Attendance_Management_System.controller;

import in.College_Attendance_Management_System.DTO.StudentDto;
import in.College_Attendance_Management_System.DTO.AttendanceDto;
import in.College_Attendance_Management_System.DTO.AttendanceFormWrapper;
import in.College_Attendance_Management_System.DTO.ReportDto;
import in.College_Attendance_Management_System.Entity.StudentEntity;
import in.College_Attendance_Management_System.Service.StudentService;
import in.College_Attendance_Management_System.Service.AttendanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;


    // --------------------------------------------------------------------------------
    // --- HOME PAGE / ADD STUDENT (add-student.html) ---------------------------------
    // --------------------------------------------------------------------------------

    @GetMapping({"/", "/add-student"})
    public String showAddStudentForm(Model model) {
        addCommonModelAttributes(model, "Add Student");
        model.addAttribute("page", "add-student");
        model.addAttribute("studentForm", new StudentDto()); // Binds an empty DTO to the form

        return "add-student";
    }

    @PostMapping("/add-student/submit")
    public String submitAddStudentForm(@ModelAttribute("studentForm") StudentDto studentDto, RedirectAttributes redirectAttributes) {
        try {
            StudentEntity savedStudent = studentService.addStudent(studentDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Student **" + savedStudent.getFullName() + "** added successfully! ID: " + savedStudent.getStudentId());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/add-student";
    }

    // --------------------------------------------------------------------------------
    // --- MARK ATTENDANCE (mark-attendance.html) -------------------------------------
    // --------------------------------------------------------------------------------

    @GetMapping("/mark-attendance")
    public String showMarkAttendanceForm(
            @RequestParam(value = "branch", required = false) String branch,
            @RequestParam(value = "semester", required = false) Integer semester,
            Model model) {

        addCommonModelAttributes(model, "Mark Attendance");
        model.addAttribute("page", "mark-attendance");

        // Set the currently selected filters back to the form
        model.addAttribute("selectedBranch", branch);
        model.addAttribute("selectedSemester", semester);

        AttendanceFormWrapper attendanceWrapper = new AttendanceFormWrapper();

        if (branch != null && semester != null) {
            List<StudentEntity> students = studentService.getStudentsForAttendance(branch, semester);

            // Populate the wrapper with DTOs for the form
            List<AttendanceDto> dtos = students.stream().map(student -> {
                AttendanceDto dto = new AttendanceDto();
                dto.setStudentId(student.getStudentId());
                dto.setFullName(student.getFullName());
                dto.setBranch(student.getBranch());
                dto.setSemester(student.getSemester());
                dto.setPresent(true); // Default to present for convenience
                return dto;
            }).collect(Collectors.toList());

            attendanceWrapper.setAttendanceList(dtos);
        }

        model.addAttribute("attendanceWrapper", attendanceWrapper);
        return "mark-attendance";
    }

@PostMapping("/mark-attendance/submit")
    public String submitAttendance(@ModelAttribute AttendanceFormWrapper attendanceWrapper, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.saveAttendanceForToday(attendanceWrapper.getAttendanceList());
            redirectAttributes.addFlashAttribute("successMessage", "Attendance saved successfully for **" + attendanceWrapper.getAttendanceList().size() + "** students!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Attendance Error: " + e.getMessage());
        }
        return "redirect:/mark-attendance";
    }


    // --------------------------------------------------------------------------------
    // --- UPDATE STUDENT (update-student.html) ---------------------------------------
    // --------------------------------------------------------------------------------

@GetMapping("/update-student")
    public String showUpdateStudentForm(@RequestParam(value = "searchId", required = false) String searchId, Model model) {
        addCommonModelAttributes(model, "Update Student");
        model.addAttribute("page", "update-student");
        model.addAttribute("searchedId", searchId); // Keep search ID in model

        if (searchId != null && !searchId.trim().isEmpty()) {
            String trimmedId = searchId.trim();

            // 🟢 Server-side validation check
            if (!trimmedId.matches("\\d+")) {
                 model.addAttribute("errorMessage", "Error: Student ID must contain only digits (0-9).");
                 return "update-student";
            }
            
            Optional<StudentEntity> studentOptional = studentService.getStudentById(trimmedId);

            if (studentOptional.isPresent()) {
                StudentEntity student = studentOptional.get();
                // Pass a DTO populated from the Entity for form binding
                model.addAttribute("foundStudent", new StudentDto(student));
            }
        }
        return "update-student";
    }

    @PostMapping("/update-student/submit")
    public String submitUpdateStudentForm(
            @ModelAttribute("foundStudent") StudentDto studentDto,
            RedirectAttributes redirectAttributes) {

        try {
            StudentEntity updatedStudent = studentService.updateStudent(studentDto.getStudentId(), studentDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Student **" + updatedStudent.getFullName() + "** (ID: " + updatedStudent.getStudentId() + ") updated successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Update Error: " + e.getMessage());
        }

        // Redirect back to the update page, showing the updated student's ID in the search box
        return "redirect:/update-student?searchId=" + studentDto.getStudentId();
    }

    // --------------------------------------------------------------------------------
    // --- DELETE STUDENT (delete-student.html) ---------------------------------------
    // --------------------------------------------------------------------------------

    @GetMapping("/delete-student")
    public String showDeleteStudentForm(@RequestParam(value = "searchId", required = false) String searchId, Model model) {
        addCommonModelAttributes(model, "Delete Student");
        model.addAttribute("page", "delete-student");
        
        if (searchId != null && !searchId.trim().isEmpty()) {
            Optional<StudentEntity> studentOptional = studentService.getStudentById(searchId.trim());

            if (studentOptional.isPresent()) {
                model.addAttribute("studentToDelete", studentOptional.get());
            } else {
                model.addAttribute("errorMessage", "Student ID **" + searchId.trim() + "** not found for deletion.");
            }
        }
        return "delete-student";
    }

    @PostMapping("/delete-student/confirm")
    public String confirmDeleteStudent(@RequestParam("studentId") String studentId, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(studentId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Student with ID **" + studentId + "** and all associated attendance records have been **PERMANENTLY DELETED**.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deletion Error: " + e.getMessage());
        }
        return "redirect:/delete-student";
    }

    // --------------------------------------------------------------------------------
    // --- VIEW STUDENTS (view-students.html) -----------------------------------------
    // --------------------------------------------------------------------------------
    
    /**
     * Shows a list of all students.
     */
    @GetMapping("/view-students")
    public String showAllStudents(Model model) {
        addCommonModelAttributes(model, "View All Students");
        model.addAttribute("page", "view-students");
        
        // Fetch all students and add them to the model
        List<StudentEntity> allStudents = studentService.getAllStudents();
        model.addAttribute("allStudents", allStudents);

        return "view-students";
    }


    // --------------------------------------------------------------------------------
    // --- VIEW REPORT (view-report.html) ---------------------------------------------
    // --------------------------------------------------------------------------------

   @GetMapping("/view-report")
    public String showViewReportForm(@RequestParam(value = "studentId", required = false) String studentId, Model model) {
        addCommonModelAttributes(model, "View Report");
        model.addAttribute("page", "view-report");
        model.addAttribute("searchedStudentId", studentId); // Keep search ID for display

        if (studentId != null && !studentId.trim().isEmpty()) {
            // 🟢 FIX: Trim the studentId before passing it to the service
            String trimmedStudentId = studentId.trim(); 
            
            try {
                // Pass the trimmed ID to the service
                ReportDto report = attendanceService.getStudentReport(trimmedStudentId);
                model.addAttribute("reportData", report);
            } catch (IllegalArgumentException e) {
                // Catches the "Student not found" error from the service
                model.addAttribute("errorMessage", "Report error: " + e.getMessage());
            }
        }

        return "view-report";
    }

    // --------------------------------------------------------------------------------
    // --- COMMON HELPERS (Needed for all pages) --------------------------------------
    // --------------------------------------------------------------------------------

    private void addCommonModelAttributes(Model model, String pageTitle) {
        List<String> AVAILABLE_BRANCHES = Arrays.asList(
            "Computer Science & Engineering",
            "Civil Engineering",
            "Mechanical Engineering",
            "Electrical Engineering",
            "Industrial Engineering",
            "Information Technology"
        );
        List<Integer> AVAILABLE_SEMESTERS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<String> AVAILABLE_GENDERS = Arrays.asList("Male", "Female", "Other");

        model.addAttribute("availableBranches", AVAILABLE_BRANCHES);
        model.addAttribute("availableSemesters", AVAILABLE_SEMESTERS);
        model.addAttribute("availableGenders", AVAILABLE_GENDERS);
        model.addAttribute("pageTitle", pageTitle);
    }
}