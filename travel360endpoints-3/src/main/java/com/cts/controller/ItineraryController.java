package com.cts.controller;

import com.cts.dto.AddBookingDTO;
import com.cts.dto.CreateItineraryDTO;
import com.cts.dto.ItineraryResponseDTO;
import com.cts.service.ItineraryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@AllArgsConstructor
@Slf4j
public class ItineraryController {

	private final ItineraryService service;

	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> createItinerary(@RequestBody @Valid CreateItineraryDTO dto) {

		log.info("Received request to create itinerary for userId: {}", dto.getUserId());

		ItineraryResponseDTO response = service.createItinerary(dto);

		log.info("Itinerary created successfully with ID: {}", response.getItineraryId());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/add-booking")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> addBookingToItinerary(@RequestBody @Valid AddBookingDTO dto) {

		log.info("Received request to add bookingId: {} to itineraryId: {}", dto.getBookingId(), dto.getItineraryId());

		ItineraryResponseDTO response = service.addBookingToItinerary(dto);

		log.info("Booking added successfully to itineraryId: {}", dto.getItineraryId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/remove-booking")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> removeBookingFromItinerary(@RequestBody @Valid AddBookingDTO dto) {

		log.info("Received request to remove bookingId: {} from itineraryId: {}", dto.getBookingId(),
				dto.getItineraryId());

		ItineraryResponseDTO response = service.removeBookingFromItinerary(dto);

		log.info("Booking removed successfully from itineraryId: {}", dto.getItineraryId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT','ADMIN')")
	public ResponseEntity<List<ItineraryResponseDTO>> getUserItineraries(@PathVariable Long userId) {

		log.info("Received request to fetch itineraries for userId: {}", userId);

		List<ItineraryResponseDTO> itineraries = service.getUserItineraries(userId);

		log.info("Total itineraries fetched for userId {}: {}", userId, itineraries.size());

		return new ResponseEntity<>(itineraries, HttpStatus.OK);
	}
	@GetMapping("/{itineraryId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT','ADMIN')")
	public ResponseEntity<ItineraryResponseDTO> getItineraryById(@PathVariable Long itineraryId,
			@RequestParam Long userId) {

		log.info("Received request to fetch itineraryId: {} for userId: {}", itineraryId, userId);

		ItineraryResponseDTO response = service.getItineraryById(itineraryId, userId);

		log.info("Itinerary fetched successfully: ID={}", itineraryId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{itineraryId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> updateItinerary(@PathVariable Long itineraryId,
			@RequestBody @Valid CreateItineraryDTO dto) {

		log.info("Received request to update itineraryId: {}", itineraryId);

		ItineraryResponseDTO response = service.updateItinerary(itineraryId, dto);

		log.info("Itinerary updated successfully with ID: {}", itineraryId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{itineraryId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<Void> deleteItinerary(@PathVariable Long itineraryId, @RequestParam Long userId) {

		log.info("Received request to delete itineraryId: {} for userId: {}", itineraryId, userId);

		service.deleteItinerary(itineraryId, userId);

		log.info("Itinerary deleted successfully with ID: {}", itineraryId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
