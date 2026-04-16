package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.service.StudentService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountManagementController {
    private final StudentService studentService;

    @PatchMapping("/student/request-deletion")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<APIResponse> requestDeletion(Authentication auth) {
        studentService.requestAccountDeletion(auth.getName());
        return ResponseEntity.ok(new APIResponse(
                200,
                "Your deletion request has been submitted for review. You will lose access once approved.",
                null
        ));
    }

    @PatchMapping("/admin/approve-deletion/{studentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    public ResponseEntity<APIResponse> approveDeletion(@PathVariable Long studentId) {
        studentService.approveDeletion(studentId);
        return ResponseEntity.ok(new APIResponse(
                200,
                "Student account successfully deactivated. Historical data preserved.",
                null
        ));
    }


}
