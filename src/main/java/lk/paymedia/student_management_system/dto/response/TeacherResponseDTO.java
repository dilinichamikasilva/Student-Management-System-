package lk.paymedia.student_management_system.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class TeacherResponseDTO {
    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private String city;
    private String phoneNumber;
    private String department;
    private String specialization;
    private String about;
}