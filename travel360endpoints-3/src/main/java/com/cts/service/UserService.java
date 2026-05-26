package com.cts.service;

import com.cts.dto.UserDTO;
import com.cts.dto.UserResponseDTO;
import com.cts.entity.User;

import java.util.List;

public interface UserService {

    UserResponseDTO register(UserDTO dto);

    UserResponseDTO login(String email, String password);

    List<User> getAllUsers();
}