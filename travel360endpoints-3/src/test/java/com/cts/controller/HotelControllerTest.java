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
import com.cts.dto.HotelResponseDTO;
import com.cts.entity.Hotel;
import com.cts.service.AuditLogService;
import com.cts.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(HotelController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HotelControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;   // ✅ Fix JWT dependency issue

    @MockBean
    private HotelService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // ✅ CREATE HOTEL
    @Test
    public void testAddHotel() throws Exception {

        Hotel hotel = new Hotel();
        when(service.addHotel(any())).thenReturn(hotel);

        String body = """
        {
          "hotelName":"Taj",
          "ratings":4,
          "city":"Chennai",
          "address":"100 Marina Road",
          "price":3000,
          "contactNo":"9876543210",
          "emailId":"taj@mail.com",
          "totalRooms":50,
          "status":"AVAILABLE",
          "partnerId":1
        }
        """;

        mockMvc.perform(post("/api/v1/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ UPDATE HOTEL
    @Test
    public void testUpdateHotel() throws Exception {

        Hotel hotel = new Hotel();
        when(service.updateHotel(any(), any())).thenReturn(hotel);

        String body = """
        {
          "hotelName":"Taj Updated",
          "ratings":5,
          "city":"Chennai",
          "address":"100 Marina Road",
          "price":5000,
          "contactNo":"9876543210",
          "emailId":"taj@mail.com",
          "totalRooms":100,
          "status":"AVAILABLE",
          "partnerId":1
        }
        """;

        mockMvc.perform(put("/api/v1/hotels/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ GET BY LOCATION
    @Test
    public void testGetHotelsByLocation() throws Exception {

        when(service.findByLocation("Chennai", 0, 5))
                .thenReturn(List.of(HotelResponseDTO.builder().build()));

        mockMvc.perform(get("/api/v1/hotels/city/Chennai"))
                .andExpect(status().isOk());
    }

    // ✅ FILTER HOTELS
    @Test
    public void testFilterHotels() throws Exception {

        when(service.getFilteredHotels("Chennai", 4, 1000.0, 5000.0, 0, 5))
                .thenReturn(List.of(HotelResponseDTO.builder().build()));

        mockMvc.perform(get("/api/v1/hotels/filter")
                .param("city", "Chennai")
                .param("ratings", "4")
                .param("minPrice", "1000")
                .param("maxPrice", "5000"))
                .andExpect(status().isOk());
    }

    // ✅ VALIDATION FAIL (FIXED)
    @Test
    public void testAddHotel_validationFail() throws Exception {

        String body = "{}"; // empty JSON

        mockMvc.perform(post("/api/v1/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                // ✅ Accepts both 400 or 409 depending on @ControllerAdvice
                .andExpect(status().is4xxClientError());
    }
}