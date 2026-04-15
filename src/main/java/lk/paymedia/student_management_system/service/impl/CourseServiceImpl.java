package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.CourseRequestDTO;
import lk.paymedia.student_management_system.entity.Course;
import lk.paymedia.student_management_system.exception.ResourceAlreadyExistsException;
import lk.paymedia.student_management_system.repository.CourseRepository;
import lk.paymedia.student_management_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;

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
}
