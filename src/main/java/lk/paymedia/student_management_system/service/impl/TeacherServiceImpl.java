package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.TeacherRequestDTO;
import lk.paymedia.student_management_system.dto.response.TeacherResponseDTO;
import lk.paymedia.student_management_system.entity.*;
import lk.paymedia.student_management_system.exception.InternalServerErrorException;
import lk.paymedia.student_management_system.exception.ResourceAlreadyExistsException;
import lk.paymedia.student_management_system.exception.ResourceNotFoundException;
import lk.paymedia.student_management_system.exception.UserNotFoundException;
import lk.paymedia.student_management_system.repository.CourseAssignmentRepository;
import lk.paymedia.student_management_system.repository.CourseRepository;
import lk.paymedia.student_management_system.repository.TeacherRepository;
import lk.paymedia.student_management_system.repository.UserRepository;
import lk.paymedia.student_management_system.service.TeacherService;
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
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final CourseAssignmentRepository courseAssignmentRepository;

    @Override
    @Transactional
    public TeacherResponseDTO createTeacherProfile(TeacherRequestDTO dto, String currentUsername) {
        log.info("Creating teacher profile and assignments for: {}", currentUsername);

        if (teacherRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Teacher profile with this email already exists.");
        }

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUsername));

        // Build Teacher Entity
        Teacher teacher = Teacher.builder()
                .employeeId(dto.getEmployeeId())
                .name(new Name(dto.getFirstName(), dto.getLastName()))
                .email(dto.getEmail())
                .address(new Address(dto.getStreet(), dto.getCity(), dto.getState(), dto.getZipCode()))
                .phoneNumber(dto.getPhoneNumber())
                .department(dto.getDepartment())
                .specialization(dto.getSpecialization())
                .about(dto.getAbout())
                .user(user)
                .courseAssignments(new HashSet<>())
                .build();

        // Map Multiple Courses to Assignments
        if (dto.getCourseIds() != null && !dto.getCourseIds().isEmpty()) {
            dto.getCourseIds().forEach(courseId -> {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

                CourseAssignment assignment = new CourseAssignment();
                assignment.setCourse(course);
                assignment.setAssignedDate(LocalDate.now());

                teacher.addCourseAssignment(assignment);
            });
        }

        try {
            Teacher savedTeacher = teacherRepository.save(teacher);

            return TeacherResponseDTO.builder()
                    .id(savedTeacher.getId())
                    .employeeId(savedTeacher.getEmployeeId())
                    .fullName(savedTeacher.getName().getFirstName() + " " + savedTeacher.getName().getLastName())
                    .email(savedTeacher.getEmail())
                    .city(savedTeacher.getAddress().getCity())
                    .phoneNumber(savedTeacher.getPhoneNumber())
                    .department(savedTeacher.getDepartment())
                    .specialization(teacher.getSpecialization())
                    .about(savedTeacher.getAbout())
                    .assignedCourses(savedTeacher.getCourseAssignments().stream()
                            .map(a -> a.getCourse().getCourseName())
                            .collect(Collectors.toSet()))
                    .build();
        } catch (Exception e) {
            log.error("Error saving teacher: {}", e.getMessage());
            throw new InternalServerErrorException("Database error during teacher registration.");
        }
    }

    @Override
    @Transactional
    public void withdrawFromCourse(Long courseId, String currentUsername) {
        log.info("Teacher {} attempting to withdraw from course ID: {}", currentUsername, courseId);

        CourseAssignment assignment = courseAssignmentRepository.findByTeacherUserUsernameAndCourseId(currentUsername, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("You are not assigned to this course."));

        courseAssignmentRepository.delete(assignment);
        log.info("Teacher {} successfully withdrawn from course ID: {}", currentUsername, courseId);
    }

    @Override
    public void addMoreCourses(Set<Long> courseIds, String name) {
        log.info("Teacher {} attempting to add courses: {}", name, courseIds);

        Teacher teacher = teacherRepository.findByUserUsername(name)
                .orElseThrow(() -> new UserNotFoundException("Teacher not found for user: " + name));

        Set<Long> existingCourseIds = teacher.getCourseAssignments().stream()
                .map(a -> a.getCourse().getId())
                .collect(Collectors.toSet());

        courseIds.forEach(courseId -> {
            if (existingCourseIds.contains(courseId)) {
                log.warn("Teacher {} is already assigned to course ID: {}", name, courseId);
                return;
            }

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

            CourseAssignment assignment = new CourseAssignment();
            assignment.setCourse(course);
            assignment.setAssignedDate(LocalDate.now());
            teacher.addCourseAssignment(assignment);

            teacherRepository.save(teacher);
            log.info("Teacher {} successfully assigned to course ID: {}", name, courseId);
        });
    }
}
