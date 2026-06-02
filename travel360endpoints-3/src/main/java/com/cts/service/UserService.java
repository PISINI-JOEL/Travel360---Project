package com.cts.service;

import com.cts.dto.AuthResponseDTO;
import com.cts.dto.UserDTO;
import com.cts.dto.UserResponseDTO;

import java.util.List;

public interface UserService {

    UserResponseDTO register(UserDTO dto);

    AuthResponseDTO login(String email, String password);

    List<UserResponseDTO> getAllUsers();
}