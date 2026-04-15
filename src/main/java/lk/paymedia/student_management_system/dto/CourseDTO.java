package lk.paymedia.student_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private String courseCode;
    private String courseName;
    private String description;
    private Boolean isPublished;

}
