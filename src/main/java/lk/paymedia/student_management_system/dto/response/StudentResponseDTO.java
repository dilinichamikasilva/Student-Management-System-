package lk.paymedia.student_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentResponseDTO {
    private Long id;
    private String studentId;
    private String fullName;
    private String email;
    private LocalDate enrollmentDate;
    private String city;
}
