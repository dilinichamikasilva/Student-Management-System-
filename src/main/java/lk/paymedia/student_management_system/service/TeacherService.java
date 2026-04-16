package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.TeacherRequestDTO;
import lk.paymedia.student_management_system.dto.response.TeacherResponseDTO;

import java.util.Set;

public interface TeacherService {
    TeacherResponseDTO createTeacherProfile(TeacherRequestDTO requestDTO, String currentUsername);

    void withdrawFromCourse(Long courseId, String name);

    void addMoreCourses(Set<Long> courseIds, String name);
}
