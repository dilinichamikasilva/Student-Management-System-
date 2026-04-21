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
        try{
            log.info("Creating new course: {} ({})", dto.getCourseName(), dto.getCourseCode());

            // Check for duplicate course code
            if (courseRepository.existsByCourseCode(dto.getCourseCode())) {
                log.warn("Course code {} already exists", dto.getCourseCode());
                throw new ResourceAlreadyExistsException("Course code already exists.");
            }

            // Build Course Entity
            Course course = Course.builder()
                    .courseCode(dto.getCourseCode())
                    .courseName(dto.getCourseName())
                    .description(dto.getDescription())
                    .credits(dto.getCredits())
                    .isPublished(!Boolean.FALSE.equals(dto.getIsPublished()))
                    .enrollments(new HashSet<>())
                    .courseAssignments(new HashSet<>())
                    .build();

            // Save course to database
            courseRepository.save(course);
            log.info("Course {} saved successfully with ID: {}", course.getCourseCode(), course.getId());

        }catch (Exception e){
            log.error("Error creating course: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Object updateCourse(Long id, CourseRequestDTO dto) {
        try{
            log.info("Updating course with ID: {}", id);

            // Check if the course exists
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

            // Update course details
            course.setCourseName(dto.getCourseName());
            course.setCredits(dto.getCredits());
            course.setDescription(dto.getDescription());
            course.setIsPublished(Objects.requireNonNullElse(dto.getIsPublished(), course.getIsPublished()));

            // Save updated course to database
            Course updatedCourse = courseRepository.save(course);
            log.info("Course {} updated successfully", updatedCourse.getCourseCode());

            return mapToResponseDTO(updatedCourse);
        }catch (Exception e){
            log.error("Error updating course with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    private CourseResponseDTO mapToResponseDTO(Course course) {
        // Map Course entity to CourseResponseDTO, including enrolled students and assigned teachers
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
        try{
            log.info("Attempting to delete course with ID: {}", id);

            // Check if the course exists
            Course course = courseRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

            // Prevent deletion if students are enrolled
            if (!course.getEnrollments().isEmpty()) {
                throw new IllegalStateException("Cannot delete course: Students are currently enrolled.");
            }

            //delete course
            courseRepository.delete(course);
            log.info("Course with ID: {} deleted successfully", id);
        }catch (Exception e){
            log.warn("Course deletion failed: {}", e.getMessage());
            throw e;
        }
    }
}
