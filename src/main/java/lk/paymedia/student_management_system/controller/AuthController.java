package lk.paymedia.student_management_system.controller;

import lk.paymedia.student_management_system.dto.UserDTO;
import lk.paymedia.student_management_system.dto.request.UserRequestDTO;
import lk.paymedia.student_management_system.dto.response.UserResponseDTO;
import lk.paymedia.student_management_system.service.AuthService;
import lk.paymedia.student_management_system.util.APIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<APIResponse> userRegister(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("userRegister");
        UserResponseDTO userResponseDTO = authService.registerUser(userRequestDTO);
        return ResponseEntity.ok(new APIResponse(201, "User registered successfully", userResponseDTO));
    }





}
