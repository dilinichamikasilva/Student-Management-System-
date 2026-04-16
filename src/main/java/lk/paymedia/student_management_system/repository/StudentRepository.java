package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    boolean existsByEmail(String email);

    Optional<Student> findByUserUsername(String currentUsername);
}
