package lk.paymedia.student_management_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate dateOfBirth;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private Set<Long> courseIds;
}