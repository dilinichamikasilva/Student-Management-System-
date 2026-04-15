package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean findByUsername(String username);
}
