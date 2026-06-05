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
import com.cts.dto.*;
import com.cts.service.AuditLogService;
import com.cts.service.ItineraryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ItineraryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItineraryControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private ItineraryService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // ✅ CREATE
    @Test
    void testCreateItinerary() throws Exception {

        when(service.createItinerary(any()))
                .thenReturn(ItineraryResponseDTO.builder().itineraryId(1L).build());

        String body = """
        {
          "userId":1,
          "tripName":"Trip",
          "description":"Test",
          "startDate":"2030-01-01",
          "endDate":"2030-01-05"
        }
        """;

        mockMvc.perform(post("/api/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ ADD BOOKING
    @Test
    void testAddBooking() throws Exception {

        when(service.addBookingToItinerary(any()))
                .thenReturn(ItineraryResponseDTO.builder().itineraryId(1L).build());

        String body = """
        {
          "itineraryId":1,
          "bookingId":10
        }
        """;

        mockMvc.perform(post("/api/itinerary/add-booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ REMOVE BOOKING
    @Test
    void testRemoveBooking() throws Exception {

        when(service.removeBookingFromItinerary(any()))
                .thenReturn(ItineraryResponseDTO.builder().itineraryId(1L).build());

        String body = """
        {
          "itineraryId":1,
          "bookingId":10
        }
        """;

        mockMvc.perform(post("/api/itinerary/remove-booking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ GET USER ITINERARIES
    @Test
    void testGetUserItineraries() throws Exception {

        when(service.getUserItineraries(1L))
                .thenReturn(List.of(ItineraryResponseDTO.builder().build()));

        mockMvc.perform(get("/api/itinerary/user/1"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID
    @Test
    void testGetById() throws Exception {

        when(service.getItineraryById(1L, 1L))
                .thenReturn(ItineraryResponseDTO.builder().itineraryId(1L).build());

        mockMvc.perform(get("/api/itinerary/1")
                .param("userId", "1"))
                .andExpect(status().isOk());
    }

    // ✅ UPDATE
    @Test
    void testUpdate() throws Exception {

        when(service.updateItinerary(eq(1L), any()))
                .thenReturn(ItineraryResponseDTO.builder().itineraryId(1L).build());

        String body = """
        {
          "userId":1,
          "tripName":"Updated",
          "description":"Test",
          "startDate":"2030-01-01",
          "endDate":"2030-01-06"
        }
        """;

        mockMvc.perform(put("/api/itinerary/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ DELETE
    @Test
    void testDelete() throws Exception {

        mockMvc.perform(delete("/api/itinerary/1")
                .param("userId", "1"))
                .andExpect(status().isNoContent());
    }

    // ✅ VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        mockMvc.perform(post("/api/itinerary")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }
}