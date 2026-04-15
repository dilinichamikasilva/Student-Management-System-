package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);
}
