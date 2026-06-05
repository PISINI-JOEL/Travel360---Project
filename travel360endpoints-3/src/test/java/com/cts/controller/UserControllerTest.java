package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.config.JWTUtil;
import com.cts.dto.*;
import com.cts.enums.Role;
import com.cts.service.AuditLogService;
import com.cts.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private UserService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // ✅ REGISTER
    @Test
    void testRegister() throws Exception {

        when(service.register(any()))
                .thenReturn(new UserResponseDTO());

        String body = """
        {
          "email":"test@mail.com",
          "password":"password123",
          "role":"CUSTOMER",
          "phoneNo":9876543210,
          "status":"ACTIVE"
        }
        """;

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ LOGIN
    @Test
    void testLogin() throws Exception {

        AuthResponseDTO res = AuthResponseDTO.builder()
                .token("token")
                .user(new UserResponseDTO())
                .build();

        when(service.login(any(), any())).thenReturn(res);

        String body = """
        {
          "email":"test@mail.com",
          "password":"password123"
        }
        """;

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        when(service.getAllUsers())
                .thenReturn(List.of(new UserResponseDTO()));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk());
    }

    // ✅ VALIDATION FAIL
    @Test
    void testRegister_validationFail() throws Exception {

        String body = "{}";

        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is4xxClientError());
    }
}