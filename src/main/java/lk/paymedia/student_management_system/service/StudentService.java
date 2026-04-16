package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.StudentRequestDTO;
import lk.paymedia.student_management_system.dto.response.StudentResponseDTO;

import java.util.Set;

public interface StudentService {
    StudentResponseDTO createStudentProfile(StudentRequestDTO requestDTO, String currentUsername);

    void dropCourseForStudent(Long courseId, String name);

    void addMoreCourses(Set<Long> courseIds, String name);
}
