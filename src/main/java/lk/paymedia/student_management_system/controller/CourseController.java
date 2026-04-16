package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.request.CourseRequestDTO;
import lk.paymedia.student_management_system.service.CourseService;
import lk.paymedia.student_management_system.service.StudentService;
import lk.paymedia.student_management_system.service.TeacherService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<APIResponse> addCourse(@RequestBody CourseRequestDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse(201, "Course created successfully", null));
    }

    @DeleteMapping("/student/drop/{courseId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<APIResponse> studentDrop(@PathVariable Long courseId, Authentication auth) {
        studentService.dropCourseForStudent(courseId, auth.getName());
        return ResponseEntity.ok(new APIResponse(200, "Course dropped successfully", null));
    }

    @DeleteMapping("/teacher/withdraw/{courseId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<APIResponse> teacherWithdraw(@PathVariable Long courseId, Authentication auth) {
        teacherService.withdrawFromCourse(courseId, auth.getName());
        return ResponseEntity.ok(new APIResponse(200, "Withdrawn from course successfully", null));
    }
}
