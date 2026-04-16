package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.UpdateMarksRequestDTO;
import lk.paymedia.student_management_system.dto.response.CourseGradeResponseDTO;

import java.util.List;

public interface GradeService {
    void updateStudentGrades(UpdateMarksRequestDTO dto, String name);

    List<CourseGradeResponseDTO> getGradesByCourse(Long courseId, String name);
}
