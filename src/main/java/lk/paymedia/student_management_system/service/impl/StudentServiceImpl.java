package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.StudentRequestDTO;
import lk.paymedia.student_management_system.dto.response.StudentResponseDTO;
import lk.paymedia.student_management_system.entity.Address;
import lk.paymedia.student_management_system.entity.Name;
import lk.paymedia.student_management_system.entity.Student;
import lk.paymedia.student_management_system.entity.User;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.UserAlreadyExistsException;
import lk.paymedia.student_management_system.exception.UserNotFoundException;
import lk.paymedia.student_management_system.repository.StudentRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudentResponseDTO createStudentProfile(StudentRequestDTO dto, String currentUsername) {
        log.info("Attempting to create student profile for user: {}", currentUsername);

        //Check if profile already exists
        if (studentRepository.existsByEmail(dto.getEmail())) {
            log.warn("Profile creation failed: Email {} already registered", dto.getEmail());
            throw new UserAlreadyExistsException("Student profile with this email already exists.");
        }

        // Fetch the User entity
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUsername));

        // Map DTO to Entity
        Student student = Student.builder()
                .studentId(dto.getStudentId())
                .email(dto.getEmail())
                .dateOfBirth(dto.getDateOfBirth())
                .enrollmentDate(LocalDate.now())
                .name(Name.builder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .build())
                .address(Address.builder()
                        .street(dto.getStreet())
                        .city(dto.getCity())
                        .state(dto.getState())
                        .zipCode(dto.getZipCode())
                        .build())
                .user(user)
                .build();

        try {
            Student savedStudent = studentRepository.save(student);
            log.info("Student profile created successfully for ID: {}", savedStudent.getStudentId());

            return StudentResponseDTO.builder()
                    .id(savedStudent.getId())
                    .studentId(savedStudent.getStudentId())
                    .fullName(savedStudent.getName().getFirstName() + " " + savedStudent.getName().getLastName())
                    .email(savedStudent.getEmail())
                    .enrollmentDate(savedStudent.getEnrollmentDate())
                    .city(savedStudent.getAddress().getCity())
                    .build();
        } catch (Exception e) {
            log.error("Critical error saving student profile: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to save student profile due to database error.");
        }
    }
}
