package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.UserRequestDTO;
import lk.paymedia.student_management_system.dto.response.UserResponseDTO;
import lk.paymedia.student_management_system.entity.Role;
import lk.paymedia.student_management_system.entity.RoleType;
import lk.paymedia.student_management_system.entity.User;
import lk.paymedia.student_management_system.entity.UserRole;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.ResourceAlreadyExistsException;
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
       try{
           log.info("Registration request received for username: {}", requestDTO.getUsername());

           // Check if user exists
           if (userRepository.existsByUsername(requestDTO.getUsername())) {
               log.warn("Registration failed: Username {} already exists", requestDTO.getUsername());
               throw new ResourceAlreadyExistsException(requestDTO.getUsername());
           }

           // Map DTO to entity
           User user = new User();
           user.setUsername(requestDTO.getUsername());
           user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
           user.setEnabled(true);

           // Roles management
           if (requestDTO.getUserRoles() != null && !requestDTO.getUserRoles().isEmpty()) {
               user.setUserRoles(requestDTO.getUserRoles().stream().map(roleName -> {
                   Role role = roleRepository.findByRoleType(RoleType.valueOf(roleName))
                           .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found."));

                   UserRole userRole = new UserRole();
                   userRole.setUser(user);
                   userRole.setRole(role);
                   return userRole;
               }).collect(Collectors.toSet()));
           }

           // Save and return the result of the save method
           return saveUserAndBuildResponse(user, requestDTO.getUserRoles());
       }catch (Exception e){
              log.error("Error during registration: {}", e.getMessage());
              throw new InternalServerErrorException("An error occurred during registration. Please try again later.");
       }
    }

    private UserResponseDTO saveUserAndBuildResponse(User user, Set<String> roleNames) {
        try {
            User savedUser = userRepository.save(user);
            log.info("User successfully saved to database with ID: {}", savedUser.getId());

            return UserResponseDTO.builder()
                    .id(savedUser.getId())
                    .username(savedUser.getUsername())
                    .enabled(savedUser.getEnabled())
                    .userRoles(roleNames)
                    .build();
        } catch (Exception e) {
            log.error("Database error while saving user: {}", e.getMessage());
            throw new InternalServerErrorException("Could not complete registration due to database error");
        }
    }


    @Override
    public UserResponseDTO login(UserRequestDTO userRequestDTO) {
        String username = userRequestDTO.getUsername();
        log.info("Login attempt initiated for user: {}", username);

        try {
            //authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            userRequestDTO.getPassword()
                    )
            );
            log.debug("AuthenticationManager successfully verified credentials for: {}", username);

            //fetch user from db
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("Authentication succeeded but user {} not found in database!", username);
                        return new RuntimeException("User record missing after authentication");
                    });

            // extract roles
            Set<String> roles = user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getRoleType().name())
                    .collect(Collectors.toSet());
            log.debug("Roles extracted for user {}: {}", username, roles);

            //generate jwt token
            String token = jwtUtil.generateToken(user.getUsername());
            log.info("JWT Token generated successfully for user: {}", username);

            //build response
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
