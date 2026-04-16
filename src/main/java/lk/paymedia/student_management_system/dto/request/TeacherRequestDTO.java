package lk.paymedia.student_management_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRequestDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String department;
    private String specialization;
    private String about;
    private Set<Long> courseIds;

}