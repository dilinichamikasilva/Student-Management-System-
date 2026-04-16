package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.StudentRequestDTO;
import lk.paymedia.student_management_system.dto.response.StudentResponseDTO;

public interface StudentService {
    StudentResponseDTO createStudentProfile(StudentRequestDTO requestDTO, String currentUsername);

    void dropCourseForStudent(Long courseId, String name);
}
