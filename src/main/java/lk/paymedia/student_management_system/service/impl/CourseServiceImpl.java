package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.CourseRequestDTO;
import lk.paymedia.student_management_system.dto.response.CourseResponseDTO;
import lk.paymedia.student_management_system.entity.Course;
import lk.paymedia.student_management_system.exception.ResourceAlreadyExistsException;
import lk.paymedia.student_management_system.exception.ResourceNotFoundException;
import lk.paymedia.student_management_system.repository.CourseRepository;
import lk.paymedia.student_management_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void createCourse(CourseRequestDTO dto) {
        log.info("Creating new course: {} ({})", dto.getCourseName(), dto.getCourseCode());

        if (courseRepository.existsByCourseCode(dto.getCourseCode())) {
            log.warn("Course code {} already exists", dto.getCourseCode());
            throw new ResourceAlreadyExistsException("Course code already exists.");
        }

        Course course = Course.builder()
                .courseCode(dto.getCourseCode())
                .courseName(dto.getCourseName())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .isPublished(!Boolean.FALSE.equals(dto.getIsPublished()))
                .enrollments(new HashSet<>())
                .courseAssignments(new HashSet<>())
                .build();

        courseRepository.save(course);
        log.info("Course {} saved successfully with ID: {}", course.getCourseCode(), course.getId());
    }

    @Override
    public Object updateCourse(Long id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        
        course.setCourseName(dto.getCourseName());
        course.setCredits(dto.getCredits());
        course.setDescription(dto.getDescription());
        course.setIsPublished(Objects.requireNonNullElse(dto.getIsPublished(), course.getIsPublished()));

        Course updatedCourse = courseRepository.save(course);
        log.info("Course {} updated successfully", updatedCourse.getCourseCode());



        return mapToResponseDTO(updatedCourse);
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .isPublished(course.getIsPublished())
                .enrolledStudents(course.getEnrollments().stream()
                        .map(e -> e.getStudent().getName().getFirstName() + " " + e.getStudent().getName().getLastName())
                        .toList())
                .assignedTeachers(course.getCourseAssignments().stream()
                        .map(a -> a.getTeacher().getName().getFirstName() + " " + a.getTeacher().getName().getLastName())
                        .toList())
                .build();
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getEnrollments().isEmpty()) {
            throw new IllegalStateException("Cannot delete course: Students are currently enrolled.");
        }

        courseRepository.delete(course);
    }
}
