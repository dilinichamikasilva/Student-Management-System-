package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.TeacherRequestDTO;
import lk.paymedia.student_management_system.dto.response.TeacherResponseDTO;
import lk.paymedia.student_management_system.entity.*;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.UserAlreadyExistsException;
import lk.paymedia.student_management_system.exception.UserNotFoundException;
import lk.paymedia.student_management_system.repository.TeacherRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    public TeacherResponseDTO createTeacherProfile(TeacherRequestDTO dto, String currentUsername) {
        log.info("Attempting to create teacher profile for user: {}", currentUsername);

        //Check if profile already exists
        if (teacherRepository.existsByEmail(dto.getEmail())) {
            log.warn("Profile creation failed: Email {} already registered", dto.getEmail());
            throw new UserAlreadyExistsException("Teacher profile with this email already exists.");
        }

        // Fetch the User entity
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUsername));

        // Map DTO to Entity
        Teacher teacher = Teacher.builder()
                .employeeId(dto.getEmployeeId())
                .name(Name.builder()
                        .firstName(dto.getFirstName())
                        .lastName(dto.getLastName())
                        .build())
                .email(dto.getEmail())
                .address(Address.builder()
                        .street(dto.getStreet())
                        .city(dto.getCity())
                        .state(dto.getState())
                        .zipCode(dto.getZipCode())
                        .build())
                .phoneNumber(dto.getPhoneNumber())
                .department(dto.getDepartment())
                .specialization(dto.getSpecialization())
                .about(dto.getAbout())
                .user(user)
                .build();

        try {
            Teacher savedTeacher = teacherRepository.save(teacher);
            log.info("Teacher profile created successfully for ID: {}", savedTeacher.getEmployeeId());

            return TeacherResponseDTO.builder()
                    .id(savedTeacher.getId())
                    .employeeId(savedTeacher.getEmployeeId())
                    .fullName(savedTeacher.getName().getFirstName() + " " + savedTeacher.getName().getLastName())
                    .email(savedTeacher.getEmail())
                    .city(savedTeacher.getAddress().getCity())
                    .phoneNumber(savedTeacher.getPhoneNumber())
                    .department(savedTeacher.getDepartment())
                    .specialization(savedTeacher.getSpecialization())
                    .about(savedTeacher.getAbout())
                    .build();
        } catch (Exception e) {
            log.error("Critical error saving teacher profile: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to save teacher profile due to database error.");
        }
    }
}
