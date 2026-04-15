package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.request.StudentRequestDTO;
import lk.paymedia.student_management_system.dto.response.StudentResponseDTO;
import lk.paymedia.student_management_system.service.StudentService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/register-profile")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<APIResponse> registerProfile(@RequestBody StudentRequestDTO requestDTO,
                                                       Authentication authentication) {

        String currentUsername = authentication.getName();
        log.info("Student profile registration request by user: {}", currentUsername);

        StudentResponseDTO response = studentService.createStudentProfile(requestDTO, currentUsername);

        return ResponseEntity.ok(new APIResponse(
                201,
                "Student profile completed successfully",
                response
        ));
    }
}
