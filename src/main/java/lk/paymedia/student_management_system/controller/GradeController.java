package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.request.UpdateMarksRequestDTO;
import lk.paymedia.student_management_system.service.GradeService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grade")
@RequiredArgsConstructor
@Slf4j
public class GradeController {
    private final GradeService gradeService;

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<APIResponse> updateMarks(
            @RequestBody UpdateMarksRequestDTO dto,
            Authentication authentication) {

        gradeService.updateStudentGrades(dto, authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Grades updated successfully", null));
    }

}
