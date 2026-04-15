package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.request.CourseRequestDTO;
import lk.paymedia.student_management_system.service.CourseService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<APIResponse> addCourse(@RequestBody CourseRequestDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse(201, "Course created successfully", null));
    }
}
