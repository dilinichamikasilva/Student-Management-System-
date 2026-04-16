package lk.paymedia.student_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String studentId;

    @Embedded
    private Name name;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDate dateOfBirth;
    private LocalDate enrollmentDate;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();


    public void addEnrollment(Enrollment enrollment) {
        if (this.enrollments == null) {
            this.enrollments = new HashSet<>();
        }
        this.enrollments.add(enrollment);
        enrollment.setStudent(this);
    }


}
