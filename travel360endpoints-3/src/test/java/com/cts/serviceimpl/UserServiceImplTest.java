package com.cts.serviceimpl;

import com.cts.config.JWTUtil;
import com.cts.dto.UserDTO;
import com.cts.entity.User;
import com.cts.enums.Role;
import com.cts.exception.UserNotFoundException;
import com.cts.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository repo;
    @Mock private PasswordEncoder encoder;
    @Mock private JWTUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl service;

    private UserDTO dto;
    private User user;

    @BeforeEach
    void setup() {

        dto = new UserDTO();
        dto.setEmail("test@mail.com");
        dto.setPassword("password123");
        dto.setRole(Role.CUSTOMER);
        dto.setPhoneNo(9876543210L);
        dto.setStatus("ACTIVE");

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("encoded");
        user.setRole(Role.CUSTOMER);
        user.setStatus("ACTIVE");
    }

    // ✅ REGISTER
    @Test
    void register_success() {

        when(encoder.encode(any())).thenReturn("encoded");
        when(repo.save(any())).thenReturn(user);

        assertNotNull(service.register(dto));
    }

    // ✅ LOGIN SUCCESS
    @Test
    void login_success() {

        when(repo.findByEmail(any())).thenReturn(user);
        when(encoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        assertNotNull(service.login("test@mail.com", "password123"));
    }

    // ✅ LOGIN FAIL (USER NULL)
    @Test
    void login_userNotFound() {

        when(repo.findByEmail(any())).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> service.login("test@mail.com", "password123"));
    }

    // ✅ LOGIN FAIL (WRONG PASSWORD)
    @Test
    void login_invalidPassword() {

        when(repo.findByEmail(any())).thenReturn(user);
        when(encoder.matches(any(), any())).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> service.login("test@mail.com", "wrong"));
    }

    // ✅ GET ALL
    @Test
    void getAllUsers() {

        when(repo.findAll()).thenReturn(List.of(user));

        assertFalse(service.getAllUsers().isEmpty());
    }
}
