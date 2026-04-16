package lk.paymedia.student_management_system.dto.response;

import lk.paymedia.student_management_system.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResultDTO {
    private String courseCode;
    private String courseName;
    private Double marks;
    private String grade;
    private Status status;
}