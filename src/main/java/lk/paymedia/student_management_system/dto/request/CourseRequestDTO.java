package lk.paymedia.student_management_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequestDTO {
    private String courseCode;
    private String courseName;
    private String description;
    private Double credits;
    private Boolean isPublished;
}
