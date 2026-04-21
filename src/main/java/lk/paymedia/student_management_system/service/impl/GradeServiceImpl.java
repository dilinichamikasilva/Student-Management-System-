package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.UpdateMarksRequestDTO;
import lk.paymedia.student_management_system.dto.response.CourseGradeResponseDTO;
import lk.paymedia.student_management_system.dto.response.MarksResponseDTO;
import lk.paymedia.student_management_system.entity.Enrollment;
import lk.paymedia.student_management_system.exception.ResourceNotFoundException;
import lk.paymedia.student_management_system.repository.EnrollmentRepository;
import lk.paymedia.student_management_system.repository.TeacherRepository;
import lk.paymedia.student_management_system.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements GradeService {

    private final EnrollmentRepository enrollmentRepository;
    private final TeacherRepository teacherRepository;


    @Override
    @Transactional
    public MarksResponseDTO updateStudentGrades(UpdateMarksRequestDTO dto, String currentTeacherUsername) {
        try{
            log.info("Teacher {} updating marks for Student ID: {} in Course ID: {}",
                    currentTeacherUsername, dto.getStudentId(), dto.getCourseId());

            boolean isAssigned = teacherRepository.existsByUsernameAndCourseId(currentTeacherUsername, dto.getCourseId());
            if (!isAssigned) {
                throw new AccessDeniedException("You are not assigned to this course.");
            }

            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Enrollment record not found for this student and course."));

            // Update marks and calculate grade
            enrollment.setMarks(dto.getMarks());
            String calculatedGrade = calculateGrade(dto.getMarks());
            enrollment.setGrade(calculatedGrade);

            if (dto.getStatus() != null) {
                enrollment.setStatus(dto.getStatus());
            }

            Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
            log.info("Marks updated and grade '{}' assigned successfully.", calculatedGrade);

            // Return the mapped DTO
            return MarksResponseDTO.builder()
                    .studentName(updatedEnrollment.getStudent().getName().getFirstName() + " " + updatedEnrollment.getStudent().getName().getLastName())
                    .courseName(updatedEnrollment.getCourse().getCourseName())
                    .marks(updatedEnrollment.getMarks())
                    .grade(updatedEnrollment.getGrade())
                    .status(updatedEnrollment.getStatus())
                    .build();
        }catch (Exception e){
            log.error("Error updating marks: {}", e.getMessage());
            throw e;
        }
    }


    private String calculateGrade(Double marks) {
        if (marks == null) {
            return "N/A";
        }

        int bucket = (int) (marks / 10);

        return switch (bucket) {
            case 8 -> "A";
            case 7 -> (marks >= 75) ? "A" : "B";
            case 6 -> (marks >= 65) ? "B" : "C";
            case 5 -> (marks >= 55) ? "C" : "S";
            case 4 -> (marks >= 45) ? "S" : "F";
            default -> "F";
        };
    }

    @Override
    public List<CourseGradeResponseDTO> getGradesByCourse(Long courseId, String teacherUsername) {
        try{
            log.info("Teacher {} requesting grades for Course ID: {}", teacherUsername, courseId);

            // Is this teacher assigned to this course?
            boolean isAssigned = teacherRepository.existsByUsernameAndCourseId(teacherUsername, courseId);
            if (!isAssigned) {
                throw new AccessDeniedException("You are not assigned to view grades for this course.");
            }

            // Fetch all enrollments for the course
            List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);

            // Map to DTO
            return enrollments.stream()
                    .map(e -> CourseGradeResponseDTO.builder()
                            .studentId(e.getStudent().getStudentId())
                            .studentName(e.getStudent().getName().getFirstName() + " " + e.getStudent().getName().getLastName())
                            .marks(e.getMarks())
                            .grade(e.getGrade())
                            .status(e.getStatus())
                            .build())
                    .toList();
        }catch (Exception e){
            log.error("Error fetching grades for course ID {}: {}", courseId, e.getMessage());
            throw e;
        }
    }
}
