package com.cts.controller;

import com.cts.dto.*;
import com.cts.service.BookingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService service;

    @PostMapping("/flight")
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER','TRAVEL_AGENT')")
    public ResponseEntity<BookingFlightResponseDTO> createFlightBooking(
            @RequestBody @Valid BookingFlightDTO dto) {

        log.info("Received request to create FLIGHT booking for userId: {}", dto.getUserId());

        BookingFlightResponseDTO response = service.createFlightBooking(dto);

        log.info("Flight booking created successfully with bookingId: {}", response.getBookingId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/hotel")
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER','TRAVEL_AGENT')")
    public ResponseEntity<BookingHotelResponseDTO> createHotelBooking(
            @RequestBody @Valid BookingHotelDTO dto) {

        log.info("Received request to create HOTEL booking for userId: {}", dto.getUserId());

        BookingHotelResponseDTO response = service.createHotelBooking(dto);

        log.info("Hotel booking created successfully with bookingId: {}", response.getBookingId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/package")
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER','TRAVEL_AGENT')")
    public ResponseEntity<BookingPackageResponseDTO> createPackageBooking(
            @RequestBody @Valid BookingPackageDTO dto) {

        log.info("Received request to create PACKAGE booking for userId: {}", dto.getUserId());

        BookingPackageResponseDTO response = service.createPackageBooking(dto);

        log.info("Package booking created successfully with bookingId: {}", response.getBookingId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/transport")
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER','TRAVEL_AGENT')")
    public ResponseEntity<BookingTransportResponseDTO> createTransportBooking(
            @RequestBody @Valid BookingTransportDTO dto) {

        log.info("Received request to create TRANSPORT booking for userId: {}", dto.getUserId());

        BookingTransportResponseDTO response = service.createTransportBooking(dto);

        log.info("Transport booking created successfully with bookingId: {}", response.getBookingId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TRAVEL_AGENT','ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getAll() {

        log.info("Fetching all bookings");

        List<BookingResponseDTO> bookings = service.getAllBookings();

        log.info("Total bookings fetched: {}", bookings.size());

        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT','ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getByUser(@PathVariable Long userId) {

        log.info("Fetching bookings for userId: {}", userId);

        List<BookingResponseDTO> bookings = service.getBookingsByUser(userId);

        log.info("Found {} bookings for userId: {}", bookings.size(), userId);

        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER','TRAVEL_AGENT')")
    public ResponseEntity<BookingCancelResponseDTO> cancelBooking(
            @RequestBody BookingCancelDTO dto) {

        log.info("Received request to cancel booking for bookingId: {}", dto.getBookingId());

        BookingCancelResponseDTO response = service.deleteBooking(dto);

        log.info("Booking cancelled successfully for bookingId: {}", dto.getBookingId());
        return new ResponseEntity<>(response,HttpStatus.OK);
        
    }

    @DeleteMapping("/{bookingId}/passengers/{passengerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER','TRAVEL_AGENT')")
    public ResponseEntity<PassengerCancelResponseDTO> cancelPassenger(
            @PathVariable Long bookingId,
            @PathVariable Long passengerId,
            @RequestParam Long userId) {

        log.info("Cancelling passengerId: {} from bookingId: {} for userId: {}",
                passengerId, bookingId, userId);

        PassengerCancelResponseDTO response =
                service.cancelPassenger(bookingId, passengerId, userId);

        log.info("Passenger cancelled successfully: passengerId={}, bookingId={}",
                passengerId, bookingId);

        return ResponseEntity.ok(response);
    }
}
