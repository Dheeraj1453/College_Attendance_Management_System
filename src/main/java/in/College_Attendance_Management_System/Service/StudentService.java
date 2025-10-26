package in.College_Attendance_Management_System.Service;

import in.College_Attendance_Management_System.DTO.StudentDto;
import in.College_Attendance_Management_System.Entity.StudentEntity;
import in.College_Attendance_Management_System.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// 🟢 NEW IMPORTS FOR SORTING
import java.util.Comparator; 

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceService attendanceService;

    public Optional<StudentEntity> getStudentById(String studentId) {
        return studentRepository.findById(studentId);
    }

    /**
     * Adds a new student to the database.
     */
    @Transactional
    public StudentEntity addStudent(StudentDto studentDto) {
        if (studentDto.getStudentId() == null || studentDto.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        
        // --- START FIX: Student ID digit-only validation ---
        String studentId = studentDto.getStudentId().trim();

        if (!studentId.matches("\\d+")) {
            throw new IllegalArgumentException("Student ID must contain only digits.");
        }
        
        if (studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student ID already exists: " + studentId);
        }
        // --- END FIX ---
        
        StudentEntity student = new StudentEntity();
        student.setStudentId(studentId);
        student.setFullName(studentDto.getFullName());
        student.setGender(studentDto.getGender());
        student.setBranch(studentDto.getBranch());
        student.setSemester(studentDto.getSemester());

        return studentRepository.save(student);
    }

    /**
     * Updates an existing student record.
     */
    @Transactional
    public StudentEntity updateStudent(String studentId, StudentDto studentDto) {
        StudentEntity existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        existingStudent.setFullName(studentDto.getFullName());
        existingStudent.setGender(studentDto.getGender());
        existingStudent.setBranch(studentDto.getBranch());
        existingStudent.setSemester(studentDto.getSemester());

        return studentRepository.save(existingStudent);
    }

    /**
     * Retrieves a list of all students, primarily for viewing/listing purposes.
     * 🟢 FIX: Sorts the list numerically by studentId to fix "11 before 2" issue.
     */
    @Transactional(readOnly = true)
    public List<StudentEntity> getAllStudents() {
        // Fetch all students without a specific database ORDER BY clause
        List<StudentEntity> students = studentRepository.findAll();
        
        // Apply numerical sorting in Java using a custom Comparator
        students.sort(Comparator.comparing(
            StudentEntity::getStudentId, 
            (s1, s2) -> {
                // Safely parse the String studentId into an Integer for comparison
                // This is safe because input validation ensures studentId contains only digits.
                return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
            }
        ));

        return students;
    }

    /**
     * Deletes a student by their ID.
     * FIX: Deletes dependent attendance records first.
     */
    @Transactional
    public void deleteStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }
        
        // 1. Delete all associated attendance records first to avoid Foreign Key violation
        attendanceService.deleteAttendanceByStudentId(studentId);
        
        // 2. Delete the student record
        studentRepository.deleteById(studentId);
    }

    /**
     * Finds students by Branch and Semester for attendance marking.
     */
    @Transactional(readOnly = true)
    public List<StudentEntity> getStudentsForAttendance(String branch, Integer semester) {
        return studentRepository.findByBranchAndSemesterOrderByFullNameAsc(branch, semester);
    }

    /**
     * Searches students by ID or Name (for reports).
     */
    @Transactional(readOnly = true)
    public List<StudentEntity> searchStudents(String query) {
        return studentRepository.findByStudentIdContainingOrFullNameContainingIgnoreCase(query, query);
    }
}