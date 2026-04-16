package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.request.TeacherRequestDTO;
import lk.paymedia.student_management_system.dto.response.TeacherResponseDTO;
import lk.paymedia.student_management_system.service.TeacherService;
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
@RequestMapping("/teacher")
@RequiredArgsConstructor
@Slf4j
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/register-profile")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<APIResponse> registerProfile(@RequestBody TeacherRequestDTO requestDTO,
                                                       Authentication authentication) {

        String currentUsername = authentication.getName();
        log.info("Teacher profile registration request by user: {}", currentUsername);

        TeacherResponseDTO response = teacherService.createTeacherProfile(requestDTO, currentUsername);

        return ResponseEntity.ok(new APIResponse(
                201,
                "Teacher profile completed successfully",
                response
        ));
    }

}
