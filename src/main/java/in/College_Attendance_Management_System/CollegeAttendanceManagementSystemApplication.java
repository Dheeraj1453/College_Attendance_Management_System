package in.College_Attendance_Management_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "in.College_Attendance_Management_System.Repository")
public class CollegeAttendanceManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollegeAttendanceManagementSystemApplication.class, args);
    }

}