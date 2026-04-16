package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.user.username = :username")
    List<Enrollment> findAllByStudentUsername(@Param("username") String username);

    List<Enrollment> findAllByCourseId(Long courseId);
}
