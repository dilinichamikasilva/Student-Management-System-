package lk.paymedia.student_management_system.repository;

import lk.paymedia.student_management_system.entity.Role;
import lk.paymedia.student_management_system.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByRoleType(RoleType roleType);
}
