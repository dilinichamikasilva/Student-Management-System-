package lk.paymedia.student_management_system.dto.request;

import lk.paymedia.student_management_system.entity.Status;
import lombok.Data;

@Data
public class UpdateMarksRequestDTO {
    private Long studentId;
    private Long courseId;
    private Double marks;
    private String grade;
    private Status status;
}
