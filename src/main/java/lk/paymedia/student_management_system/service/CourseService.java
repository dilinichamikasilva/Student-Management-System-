package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.CourseRequestDTO;

public interface CourseService {
    void createCourse(CourseRequestDTO dto);
}
