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
import com.cts.entity.Flight;
import com.cts.service.AuditLogService;
import com.cts.service.FlightService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FlightControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private FlightService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // ✅ ADD FLIGHT
    @Test
    public void testAddFlight() throws Exception {

        Flight flight = new Flight();
        when(service.addFlight(any())).thenReturn(flight);

        String body = """
        {
          "flightNumber":"AA-123",
          "partnerId":1,
          "source":"Chennai",
          "destination":"Delhi",
          "totalSeats":100,
          "price":5000,
          "status":"SCHEDULED",
          "arrivalTime":"10:00:00",
          "departureTime":"08:00:00"
        }
        """;

        mockMvc.perform(post("/api/v1/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ UPDATE FLIGHT
    @Test
    public void testUpdateFlight() throws Exception {

        Flight flight = new Flight();
        when(service.updateFlight(any(), any())).thenReturn(flight);

        String body = """
        {
          "flightNumber":"AA-123",
          "partnerId":1,
          "source":"Chennai",
          "destination":"Delhi",
          "totalSeats":100,
          "price":5000,
          "status":"SCHEDULED",
          "arrivalTime":"10:00:00",
          "departureTime":"08:00:00"
        }
        """;

        mockMvc.perform(put("/api/v1/flights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID
    @Test
    public void testGetById() throws Exception {

        Flight flight = new Flight();
        when(service.getFlightById(1L)).thenReturn(flight);

        mockMvc.perform(get("/api/v1/flights/1"))
                .andExpect(status().isOk());
    }

    // ✅ GET ALL
    @Test
    public void testGetAllFlights() throws Exception {

        when(service.getAllFlights(0, 5))
                .thenReturn(List.of(new Flight()));

        mockMvc.perform(get("/api/v1/flights"))
                .andExpect(status().isOk());
    }

    // ✅ SEARCH
    @Test
    public void testSearchFlights() throws Exception {

        when(service.searchFlights("Chennai", "Delhi", 0, 5))
                .thenReturn(List.of(new Flight()));

        mockMvc.perform(get("/api/v1/flights/search")
                .param("source", "Chennai")
                .param("destination", "Delhi"))
                .andExpect(status().isOk());
    }

    // ✅ FILTER
    @Test
    public void testFilterFlights() throws Exception {

        when(service.filterFlights("Chennai", "Delhi", 1000.0, 5000.0, 0, 5))
                .thenReturn(List.of(new Flight()));

        mockMvc.perform(get("/api/v1/flights/filter")
                .param("source", "Chennai")
                .param("destination", "Delhi")
                .param("min", "1000")
                .param("max", "5000"))
                .andExpect(status().isOk());
    }
}