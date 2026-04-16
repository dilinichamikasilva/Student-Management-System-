package lk.paymedia.student_management_system.service.impl;

import lk.paymedia.student_management_system.dto.response.StudentResultDTO;
import lk.paymedia.student_management_system.entity.Enrollment;
import lk.paymedia.student_management_system.repository.EnrollmentRepository;
import lk.paymedia.student_management_system.service.ResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<StudentResultDTO> getMyResults(String username) {
        // Fetch all enrollments for the student
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentUsername(username);

        // Map enrollments to StudentResultDTO
        return enrollments.stream()
                .map(e -> StudentResultDTO.builder()
                        .courseCode(e.getCourse().getCourseCode())
                        .courseName(e.getCourse().getCourseName())
                        .marks(e.getMarks())
                        .grade(e.getGrade())
                        .status(e.getStatus())
                        .build())
                .toList();
    }
}
