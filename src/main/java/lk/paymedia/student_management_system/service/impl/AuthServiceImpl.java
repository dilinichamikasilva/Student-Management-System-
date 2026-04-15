package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.UserRequestDTO;
import lk.paymedia.student_management_system.dto.response.UserResponseDTO;
import lk.paymedia.student_management_system.entity.Role;
import lk.paymedia.student_management_system.entity.RoleType;
import lk.paymedia.student_management_system.entity.User;
import lk.paymedia.student_management_system.entity.UserRole;
import lk.paymedia.student_management_system.exception.UserAlreadyExistsException;
import lk.paymedia.student_management_system.repository.RoleRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        try{
            if(userRepository.findByUsername(requestDTO.getUsername())){
                throw new UserAlreadyExistsException( requestDTO.getUsername());
            }

            User user = new User();
            user.setUsername(requestDTO.getUsername());
            user.setPassword(requestDTO.getPassword());
            user.setEnabled(true);

            if(requestDTO.getUserRoles() != null){
                user.setUserRoles(requestDTO.getUserRoles().stream().map(roleName -> {
                    Role role = roleRepository.findByRoleType(RoleType.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    return userRole;
                }).collect(Collectors.toSet()));
            }

            User savedUser = userRepository.save(user);

            return UserResponseDTO.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .enabled(savedUser.getEnabled())
                    .userRoles(requestDTO.getUserRoles())
                    .build();

        }catch (Exception e){
            throw new IllegalArgumentException("An error occurred during user registration: " + e.getMessage());
        }
    }
}
