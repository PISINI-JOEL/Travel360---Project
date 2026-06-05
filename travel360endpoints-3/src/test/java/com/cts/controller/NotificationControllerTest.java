package com.cts.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.config.JWTUtil;
import com.cts.dto.NotificationResponseDTO;
import com.cts.service.NotificationService;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private NotificationService service;

    @Autowired
    private MockMvc mockMvc;

    // ✅ GET USER NOTIFICATIONS
    @Test
    void testGetUserNotifications() throws Exception {

        when(service.getUserNotifications(1L))
                .thenReturn(List.of(
                        NotificationResponseDTO.builder()
                                .notificationId(1L)
                                .message("Test")
                                .build()
                ));

        mockMvc.perform(get("/api/v1/notifications/1"))
                .andExpect(status().isOk());
    }
}