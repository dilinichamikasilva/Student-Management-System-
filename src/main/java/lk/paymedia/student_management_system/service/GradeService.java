package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.UpdateMarksRequestDTO;

public interface GradeService {
    void updateStudentGrades(UpdateMarksRequestDTO dto, String name);
}
