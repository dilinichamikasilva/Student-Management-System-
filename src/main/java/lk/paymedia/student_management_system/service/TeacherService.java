package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.TeacherRequestDTO;
import lk.paymedia.student_management_system.dto.response.TeacherResponseDTO;

public interface TeacherService {
    TeacherResponseDTO createTeacherProfile(TeacherRequestDTO requestDTO, String currentUsername);
}
