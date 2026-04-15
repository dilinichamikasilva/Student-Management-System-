package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.StudentRequestDTO;
import lk.paymedia.student_management_system.dto.response.StudentResponseDTO;
import lk.paymedia.student_management_system.entity.*;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.ResourceAlreadyExistsException;
import lk.paymedia.student_management_system.exception.UserNotFoundException;
import lk.paymedia.student_management_system.repository.CourseRepository;
import lk.paymedia.student_management_system.repository.StudentRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public StudentResponseDTO createStudentProfile(StudentRequestDTO dto, String currentUsername) {
        log.info("Creating profile and enrolling user: {}", currentUsername);

        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Student profile with this email already exists.");
        }

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUsername));

        // Build the Student base entity
        Student student = Student.builder()
                .studentId(dto.getStudentId())
                .email(dto.getEmail())
                .dateOfBirth(dto.getDateOfBirth())
                .enrollmentDate(LocalDate.now())
                .name(new Name(dto.getFirstName(), dto.getLastName()))
                .address(new Address(dto.getStreet(), dto.getCity(), dto.getState(), dto.getZipCode()))
                .user(user)
                .enrollments(new HashSet<>())
                .build();

        // Handle Course Enrollments
        if (dto.getCourseIds() != null && !dto.getCourseIds().isEmpty()) {
            log.info("Processing enrollments for {} courses", dto.getCourseIds().size());

            dto.getCourseIds().forEach(courseId -> {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

                Enrollment enrollment = new Enrollment();
                enrollment.setStudent(student);
                enrollment.setCourse(course);
                enrollment.setEnrolledDate(LocalDate.now());
                enrollment.setStatus(Status.ONGOING);

                student.getEnrollments().add(enrollment);
            });
        }

        try {
            Student savedStudent = studentRepository.save(student);
            log.info("Student and enrollments saved successfully.");

            return StudentResponseDTO.builder()
                    .id(savedStudent.getId())
                    .studentId(savedStudent.getStudentId())
                    .fullName(savedStudent.getName().getFirstName() + " " + savedStudent.getName().getLastName())
                    .email(savedStudent.getEmail())
                    .enrolledCourses(savedStudent.getEnrollments().stream()
                            .map(e -> e.getCourse().getCourseName())
                            .collect(Collectors.toSet()))
                    .build();

        } catch (Exception e) {
            log.error("Error during student registration: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to complete registration and enrollment.");
        }
    }
}
