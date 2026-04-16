package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.StudentRequestDTO;
import lk.paymedia.student_management_system.dto.response.StudentResponseDTO;
import lk.paymedia.student_management_system.entity.*;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.ResourceAlreadyExistsException;
import lk.paymedia.student_management_system.exception.ResourceNotFoundException;
import lk.paymedia.student_management_system.exception.UserNotFoundException;
import lk.paymedia.student_management_system.repository.CourseRepository;
import lk.paymedia.student_management_system.repository.EnrollmentRepository;
import lk.paymedia.student_management_system.repository.StudentRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

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
            dto.getCourseIds().forEach(courseId -> {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

                Enrollment enrollment = new Enrollment();
                enrollment.setCourse(course);
                enrollment.setEnrolledDate(LocalDate.now());
                enrollment.setStatus(Status.ONGOING);

                student.addEnrollment(enrollment);
            });
            studentRepository.save(student);
        }

        try {
            Student savedStudent = studentRepository.save(student);
            log.info("Student and enrollments saved successfully.");

            return StudentResponseDTO.builder()
                    .id(savedStudent.getId())
                    .studentId(savedStudent.getStudentId())
                    .fullName(savedStudent.getName().getFirstName() + " " + savedStudent.getName().getLastName())
                    .email(savedStudent.getEmail())
                    .enrollmentDate(savedStudent.getEnrollmentDate())
                    .city(savedStudent.getAddress().getCity())
                    .enrolledCourses(savedStudent.getEnrollments().stream()
                            .map(e -> e.getCourse().getCourseName())
                            .collect(Collectors.toSet()))
                    .build();

        } catch (Exception e) {
            log.error("Error during student registration: {}", e.getMessage());
            throw new InternalServerErrorException("Failed to complete registration and enrollment.");
        }
    }

    @Override
    @Transactional
    public void dropCourseForStudent(Long courseId, String currentUsername) {
        log.info("Student {} attempting to drop course ID: {}", currentUsername, courseId);

        // find enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentUserUsernameAndCourseId(currentUsername, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("You are not enrolled in this course."));

        // Only allow dropping if the course is not completed
        if (Status.COMPLETED.equals(enrollment.getStatus())) {
            throw new IllegalStateException("Cannot drop a course that is already completed.");
        }

        enrollmentRepository.delete(enrollment);
        log.info("Course ID: {} dropped successfully for student: {}", courseId, currentUsername);
    }

    @Override
    @Transactional
    public void addMoreCourses(Set<Long> newCourseIds, String currentUsername) {
        log.info("Student {} adding {} more courses", currentUsername, newCourseIds.size());

        // Fetch the Student profile
        Student student = studentRepository.findByUserUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please register first."));

        Set<Long> existingCourseIds = student.getEnrollments().stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toSet());

        // Add only the new courses
        newCourseIds.stream()
                .filter(courseId -> !existingCourseIds.contains(courseId))
                .forEach(courseId -> {
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

                    Enrollment enrollment = new Enrollment();
                    enrollment.setCourse(course);
                    enrollment.setEnrolledDate(LocalDate.now());
                    enrollment.setStatus(Status.ONGOING);

                    student.addEnrollment(enrollment);
                });

        studentRepository.save(student);
        log.info("Extra courses successfully added for student: {}", currentUsername);
    }

    @Override
    @Transactional
    public void requestAccountDeletion(String name) {
        Student student = studentRepository.findByUserUsername(name)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setAccountStatus(AccountStatus.DELETION_PENDING);
        studentRepository.save(student);
        log.info("Deletion request submitted for student: {}", name);
    }

    @Override
    @Transactional
    public void approveDeletion(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getAccountStatus() != AccountStatus.DELETION_PENDING) {
            throw new IllegalStateException("Only pending deletion requests can be approved.");
        }

        // Update Student status
        student.setAccountStatus(AccountStatus.DEACTIVATED);

        // soft delete
        User user = student.getUser();
        user.setDeleted(true);

        studentRepository.save(student);
        userRepository.save(user);

        log.info("Student account {} successfully deactivated and locked.", studentId);
    }
}
