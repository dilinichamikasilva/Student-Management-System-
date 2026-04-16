package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(t) > 0 FROM Teacher t " +
            "JOIN t.user u " +
            "JOIN t.courseAssignments ca " +
            "WHERE u.username = :username AND ca.course.id = :courseId")
    boolean existsByUsernameAndCourseId(@Param("username")String username, Long courseId);

    Optional<Teacher> findByUserUsername(String name);
}
