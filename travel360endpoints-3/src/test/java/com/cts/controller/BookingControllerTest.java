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
import com.cts.service.AuditLogService;
import com.cts.service.BookingService;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private BookingService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    // ✅ CREATE FLIGHT
    @Test
    void testCreateFlightBooking() throws Exception {

        when(service.createFlightBooking(any()))
                .thenReturn(BookingFlightResponseDTO.builder().bookingId(1L).build());

        String body = """
        {
          "userId":1,
          "flightId":10,
          "travelDate":"2030-01-01",
          "units":1,
          "bookingName":"John",
          "gender":"MALE",
          "passengers":[{
            "passengerName":"John Doe",
            "dateOfBirth":"2000-01-01",
            "gender":"MALE",
            "contactNo":"9876543210",
            "emailAddress":"john@mail.com",
            "nationality":"Indian",
            "identificationNumber":"123456789012"
          }]
        }
        """;

        mockMvc.perform(post("/api/v1/bookings/flight")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ CREATE HOTEL
    @Test
    void testCreateHotelBooking() throws Exception {

        when(service.createHotelBooking(any()))
                .thenReturn(BookingHotelResponseDTO.builder().bookingId(2L).build());

        String body = """
        {
          "userId":1,
          "hotelId":20,
          "checkInDate":"2030-01-01",
          "checkOutDate":"2030-01-03",
          "units":1,
          "bookingName":"John",
          "gender":"MALE"
        }
        """;

        mockMvc.perform(post("/api/v1/bookings/hotel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ CREATE PACKAGE
    @Test
    void testCreatePackageBooking() throws Exception {

        when(service.createPackageBooking(any()))
                .thenReturn(BookingPackageResponseDTO.builder().bookingId(3L).build());

        String body = """
        {
          "userId":1,
          "packageId":30,
          "units":1,
          "bookingName":"John",
          "gender":"MALE"
        }
        """;

        mockMvc.perform(post("/api/v1/bookings/package")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ CREATE TRANSPORT
    @Test
    void testCreateTransportBooking() throws Exception {

        when(service.createTransportBooking(any()))
                .thenReturn(BookingTransportResponseDTO.builder().bookingId(4L).build());

        String body = """
        {
          "userId":1,
          "transportId":40,
          "travelDate":"2030-01-01",
          "units":1,
          "bookingName":"John",
          "gender":"MALE",
          "passengers":[{
            "passengerName":"John Doe",
            "dateOfBirth":"2000-01-01",
            "gender":"MALE",
            "contactNo":"9876543210",
            "emailAddress":"john@mail.com",
            "nationality":"Indian",
            "identificationNumber":"123456789012"
          }]
        }
        """;

        mockMvc.perform(post("/api/v1/bookings/transport")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        when(service.getAllBookings())
                .thenReturn(List.of(BookingResponseDTO.builder().bookingId(1L).build()));

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY USER
    @Test
    void testGetByUser() throws Exception {

        when(service.getBookingsByUser(1L))
                .thenReturn(List.of(BookingResponseDTO.builder().bookingId(1L).build()));

        mockMvc.perform(get("/api/v1/bookings/user/1"))
                .andExpect(status().isOk());
    }

    // ✅ CANCEL BOOKING
    @Test
    void testCancelBooking() throws Exception {

        when(service.deleteBooking(any()))
                .thenReturn(BookingCancelResponseDTO.builder().bookingId(1L).build());

        String body = """
        {
          "bookingId":1,
          "userId":1
        }
        """;

        mockMvc.perform(post("/api/v1/bookings/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ CANCEL PASSENGER
    @Test
    void testCancelPassenger() throws Exception {

        when(service.cancelPassenger(any(), any(), any()))
                .thenReturn(PassengerCancelResponseDTO.builder().bookingId(1L).passengerId(5L).build());

        mockMvc.perform(delete("/api/v1/bookings/1/passengers/5")
                .param("userId", "1"))
                .andExpect(status().isOk());
    }
}
