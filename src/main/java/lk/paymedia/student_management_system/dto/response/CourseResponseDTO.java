package lk.paymedia.student_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseResponseDTO {
    private Long id;
    private String courseCode;
    private String courseName;
    private String description;
    private Double credits;
    private Boolean isPublished;

    private List<String> enrolledStudents;
    private List<String> assignedTeachers;

}
