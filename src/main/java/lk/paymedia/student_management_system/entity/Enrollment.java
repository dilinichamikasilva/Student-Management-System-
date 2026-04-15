package lk.paymedia.student_management_system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "enrollements")
@Data
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate enrolledDate;
    private Double marks;
    private String grade;

    @Enumerated(EnumType.STRING)
    private Status status;
}
