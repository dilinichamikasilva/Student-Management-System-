package lk.paymedia.student_management_system.service;

import lk.paymedia.student_management_system.dto.response.StudentResultDTO;

import java.util.List;

public interface ResultService {
    List<StudentResultDTO> getMyResults(String name);
}
