package com.cts.controller;

import com.cts.dto.BookingDTO;
import com.cts.dto.BookingFlightDTO;
import com.cts.dto.BookingFlightResponseDTO;
import com.cts.dto.BookingHotelDTO;
import com.cts.dto.BookingHotelResponseDTO;
import com.cts.dto.BookingPackageDTO;
import com.cts.dto.BookingPackageResponseDTO;
import com.cts.dto.BookingResponseDTO;
import com.cts.dto.BookingTransportDTO;
import com.cts.dto.BookingTransportResponseDTO;
import com.cts.service.BookingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@AllArgsConstructor
public class BookingController {

	private final BookingService service;

	@PostMapping("/flight")
	public ResponseEntity<BookingFlightResponseDTO> createFlightBooking(@RequestBody @Valid BookingFlightDTO dto) {

		return new ResponseEntity<>(service.createFlightBooking(dto), HttpStatus.CREATED);
	}

	@PostMapping("/hotel")
	public ResponseEntity<BookingHotelResponseDTO> createHotelBooking(@RequestBody @Valid BookingHotelDTO dto) {

		return new ResponseEntity<>(service.createHotelBooking(dto), HttpStatus.CREATED);
	}

	@PostMapping("/package")
	public ResponseEntity<BookingPackageResponseDTO> createPackageBooking(@RequestBody @Valid BookingPackageDTO dto) {

		return new ResponseEntity<>(service.createPackageBooking(dto), HttpStatus.CREATED);
	}

	@PostMapping("/transport")
	public ResponseEntity<BookingTransportResponseDTO> createTransportBooking(@RequestBody @Valid BookingTransportDTO dto) {

		return new ResponseEntity<>(service.createTransportBooking(dto), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<BookingResponseDTO>> getAll() {

		return new ResponseEntity<>(service.getAllBookings(), HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<BookingResponseDTO>> getByUser(@PathVariable Long userId) {

		return new ResponseEntity<>(service.getBookingsByUser(userId), HttpStatus.OK);
	}

}
