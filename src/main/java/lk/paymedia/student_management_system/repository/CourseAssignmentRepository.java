package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
    Optional<CourseAssignment> findByTeacherUserUsernameAndCourseId(String currentUsername, Long courseId);
}
