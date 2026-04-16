package lk.paymedia.student_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String employeeId;

    @Embedded
    private Name name;

    @Column(unique = true, nullable = false)
    private String email;

    @Embedded
    private Address address;

    private String phoneNumber;

    @Column(nullable = false)
    private String department;

    private String specialization;

    private String about;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseAssignment> courseAssignments = new HashSet<>();

    public void addCourseAssignment(CourseAssignment assignment) {
        if (this.courseAssignments == null) {
            this.courseAssignments = new HashSet<>();
        }
        this.courseAssignments.add(assignment);
        assignment.setTeacher(this);
    }


}
