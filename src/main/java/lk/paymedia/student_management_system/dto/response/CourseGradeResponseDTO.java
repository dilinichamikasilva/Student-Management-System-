package lk.paymedia.student_management_system.dto.response;

import lk.paymedia.student_management_system.entity.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseGradeResponseDTO {
    private String studentId;
    private String studentName;
    private Double marks;
    private String grade;
    private Status status;
}
