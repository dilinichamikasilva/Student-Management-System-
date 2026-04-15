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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new UserAlreadyExistsException(requestDTO.getUsername());
        }

        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEnabled(true);

        if (requestDTO.getUserRoles() != null && !requestDTO.getUserRoles().isEmpty()) {
            user.setUserRoles(requestDTO.getUserRoles().stream().map(roleName -> {
                Role role = roleRepository.findByRoleType(RoleType.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found in DB. Please seed the roles table first."));

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
    }
}
