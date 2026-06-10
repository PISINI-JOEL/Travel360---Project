package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.cts.dto.TransportResponseDTO;
import com.cts.entity.Transport;
import com.cts.enums.TransportStatus;
import com.cts.service.AuditLogService;
import com.cts.service.TransportService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TransportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransportControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private TransportService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // ✅ ADD
    @Test
    void testAddTransport() throws Exception {

        when(service.addTransport(any()))
                .thenReturn(Transport.builder().transportId(1L).build());

        String body = """
        {
          "transportNumber":123,
          "source":"Chennai",
          "destination":"Delhi",
          "transportType":"BUS",
          "departureTime":"10:00:00",
          "arrivalTime":"20:00:00",
          "transportTotalSeats":50,
          "price":1500,
          "transportStatus":"AVAILABLE",
          "partnerId":1
        }
        """;

        mockMvc.perform(post("/api/v1/transports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ UPDATE
    @Test
    void testUpdateTransport() throws Exception {

        when(service.updateTransport(eq(1L), any()))
                .thenReturn(Transport.builder().transportId(1L).build());

        String body = """
        {
          "transportNumber":123,
          "source":"Chennai",
          "destination":"Delhi",
          "transportType":"BUS",
          "departureTime":"10:00:00",
          "arrivalTime":"20:00:00",
          "transportTotalSeats":50,
          "price":1500,
          "transportStatus":"AVAILABLE",
          "partnerId":1
        }
        """;

        mockMvc.perform(put("/api/v1/transports/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        when(service.getAllTransports(0, 5))
                .thenReturn(List.of(TransportResponseDTO.builder().build()));

        mockMvc.perform(get("/api/v1/transports"))
                .andExpect(status().isOk());
    }

    // ✅ SEARCH ROUTE
    @Test
    void testFindByRoute() throws Exception {

        when(service.findByRoute("Chennai", "Delhi", 0, 5))
                .thenReturn(List.of(TransportResponseDTO.builder().build()));

        mockMvc.perform(get("/api/v1/transports/search")
                .param("source", "Chennai")
                .param("destination", "Delhi"))
                .andExpect(status().isOk());
    }

    // ✅ FIND BY STATUS
    @Test
    void testFindByStatus() throws Exception {

        when(service.findByStatus(TransportStatus.AVAILABLE, 0, 5))
                .thenReturn(List.of(TransportResponseDTO.builder().build()));

        mockMvc.perform(get("/api/v1/transports/status/AVAILABLE"))
                .andExpect(status().isOk());
    }

    // ✅ VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        String body = "{}";

        mockMvc.perform(post("/api/v1/transports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is4xxClientError());
    }
}