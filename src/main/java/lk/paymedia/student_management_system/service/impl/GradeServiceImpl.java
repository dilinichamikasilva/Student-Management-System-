package lk.paymedia.student_management_system.service.impl;

import jakarta.transaction.Transactional;
import lk.paymedia.student_management_system.dto.request.UpdateMarksRequestDTO;
import lk.paymedia.student_management_system.entity.Enrollment;
import lk.paymedia.student_management_system.exception.ResourceNotFoundException;
import lk.paymedia.student_management_system.repository.EnrollmentRepository;
import lk.paymedia.student_management_system.repository.StudentRepository;
import lk.paymedia.student_management_system.repository.TeacherRepository;
import lk.paymedia.student_management_system.service.GradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeServiceImpl implements GradeService {

    private final EnrollmentRepository enrollmentRepository;
    private final TeacherRepository teacherRepository;


    @Override
    @Transactional
    public void updateStudentGrades(UpdateMarksRequestDTO dto, String currentTeacherUsername) {
        log.info("Teacher {} updating marks for Student ID: {} in Course ID: {}",
                currentTeacherUsername, dto.getStudentId(), dto.getCourseId());

        boolean isAssigned = teacherRepository.existsByUsernameAndCourseId(currentTeacherUsername, dto.getCourseId());
        if (!isAssigned) {
            throw new AccessDeniedException("You are not assigned to this course.");
        }

        // Find the Enrollment record
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment record not found for this student and course."));

        // Update fields
        enrollment.setMarks(dto.getMarks());
        enrollment.setGrade(dto.getGrade());

        if (dto.getStatus() != null) {
            enrollment.setStatus(dto.getStatus());
        }

        // Save
        enrollmentRepository.save(enrollment);
        log.info("Marks updated successfully.");
    }
}
