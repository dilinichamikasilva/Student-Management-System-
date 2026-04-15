package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.request.UserRequestDTO;
import lk.paymedia.student_management_system.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

    UserResponseDTO login(UserRequestDTO userRequestDTO);
}
