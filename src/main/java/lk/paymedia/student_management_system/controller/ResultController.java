package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.response.StudentResultDTO;
import lk.paymedia.student_management_system.service.ResultService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/results")
@RequiredArgsConstructor
@Slf4j
public class ResultController {
    private final ResultService resultService;

    @GetMapping("/my-grades")
    public ResponseEntity<APIResponse> getMyGrades(Authentication authentication) {
        List<StudentResultDTO> results = resultService.getMyResults(authentication.getName());
        return ResponseEntity.ok(new APIResponse(200, "Results retrieved successfully", results));
    }
}
