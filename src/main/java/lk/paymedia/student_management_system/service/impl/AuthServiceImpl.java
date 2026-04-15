package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.UserRequestDTO;
import lk.paymedia.student_management_system.dto.response.UserResponseDTO;
import lk.paymedia.student_management_system.entity.Role;
import lk.paymedia.student_management_system.entity.RoleType;
import lk.paymedia.student_management_system.entity.User;
import lk.paymedia.student_management_system.entity.UserRole;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.UserAlreadyExistsException;
import lk.paymedia.student_management_system.repository.RoleRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.AuthService;
import lk.paymedia.student_management_system.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import lk.paymedia.student_management_system.exception.AuthenticationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {
        log.info("Registration request received for username: {}", requestDTO.getUsername());

        //Check if user exists
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            log.warn("Registration failed: Username {} already exists", requestDTO.getUsername());
            throw new UserAlreadyExistsException(requestDTO.getUsername());
        }

        //Map DTO to Entity
        User user = new User();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEnabled(true);
        log.debug("Password encoded for user: {}", requestDTO.getUsername());

        //Roles managing
        if (requestDTO.getUserRoles() != null && !requestDTO.getUserRoles().isEmpty()) {
            log.info("Assigning roles {} to user {}", requestDTO.getUserRoles(), requestDTO.getUsername());

            user.setUserRoles(requestDTO.getUserRoles().stream().map(roleName -> {
                Role role = roleRepository.findByRoleType(RoleType.valueOf(roleName))
                        .orElseThrow(() -> {
                            log.error("Role lookup failed: {} not found in database", roleName);
                            return new RuntimeException("Role " + roleName + " not found in DB.");
                        });

                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                return userRole;
            }).collect(Collectors.toSet()));
        } else {
            log.warn("No roles provided for user: {}", requestDTO.getUsername());
        }

        //Save to Database
        try {
            User savedUser = userRepository.save(user);
            log.info("User successfully saved to database with ID: {}", savedUser.getId());

            return UserResponseDTO.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .enabled(savedUser.getEnabled())
                    .userRoles(requestDTO.getUserRoles())
                    .build();

        } catch (Exception e) {
            log.error("Database error while saving user {}: {}", requestDTO.getUsername(), e.getMessage());
            throw new IllegalArgumentException("Internal Server Error: Could not complete registration");
        }
    }

    @Override
    public UserResponseDTO login(UserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        log.info("Login attempt initiated for user: {}", username);

        try {
            //Authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            userRequestDTO.getPassword()
                    )
            );
            log.debug("AuthenticationManager successfully verified credentials for: {}", username);

            //Fetch User from Database
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Authentication succeeded but user {} not found in database!", username);
                        return new RuntimeException("User record missing after authentication");
                    });

            // Extract Role Names
            Set<String> roles = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getRoleType().name())
                    .collect(Collectors.toSet());
            log.debug("Roles extracted for user {}: {}", username, roles);

            //Generate JWT Token
            String token = jwtUtil.generateToken(user.getUsername());
            log.info("JWT Token generated successfully for user: {}", username);

            // Build Response
            return UserResponseDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .enabled(user.getEnabled())
                    .userRoles(roles)
                    .token(token)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Login failed: Invalid credentials for user '{}'", username);
            throw new BadCredentialsException("Invalid username or password");

        } catch (DisabledException e) {
            log.warn("Login failed: Account for user '{}' is disabled", username);
            throw new DisabledException("Your account has been disabled. Please contact admin.");

        } catch (AuthenticationException e) {
            log.error("Login failed: Authentication error for user '{}': {}", username, e.getMessage());
            throw new AuthenticationException("Authentication failed: " + e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error during login for user '{}': ", username, e);
            throw new InternalServerErrorException("An internal error occurred during login");
        }
    }
}
