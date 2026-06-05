package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.AddBookingDTO;
import com.cts.dto.CreateItineraryDTO;
import com.cts.dto.ItineraryResponseDTO;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.ItineraryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Itinerary Controller", description = "Build, update, and manage user trip itineraries by grouping bookings")
@Slf4j
public class ItineraryController {

	private final ItineraryService service;
	private final AuthenticatedUserProvider authUser;
	private final AuditLogService auditLogService;

	@Operation(summary = "Create a new itinerary for a user")
	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> createItinerary(@RequestBody @Valid CreateItineraryDTO dto) {

		log.info("Received request to create itinerary for userId: {}", dto.getUserId());
		auditLogService.logAction(AuditActions.CREATE_ITINERARY, AuditEntity.ITINERARY, null, authUser.currentOrNull(), LogType.INFO);

		ItineraryResponseDTO response = service.createItinerary(dto);

		log.info("Itinerary created successfully with ID: {}", response.getItineraryId());

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(summary = "Attach an existing booking to an itinerary")
	@PostMapping("/add-booking")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> addBookingToItinerary(@RequestBody @Valid AddBookingDTO dto) {

		log.info("Received request to add bookingId: {} to itineraryId: {}", dto.getBookingId(), dto.getItineraryId());
		auditLogService.logAction(AuditActions.ADD_BOOKING_TO_ITINERARY, AuditEntity.ITINERARY, dto.getItineraryId(), authUser.currentOrNull(), LogType.INFO);

		ItineraryResponseDTO response = service.addBookingToItinerary(dto);

		log.info("Booking added successfully to itineraryId: {}", dto.getItineraryId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Detach a booking from an itinerary")
	@PostMapping("/remove-booking")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> removeBookingFromItinerary(@RequestBody @Valid AddBookingDTO dto) {

		log.info("Received request to remove bookingId: {} from itineraryId: {}", dto.getBookingId(),
				dto.getItineraryId());
		auditLogService.logAction(AuditActions.REMOVE_BOOKING_FROM_ITINERARY, AuditEntity.ITINERARY, dto.getItineraryId(), authUser.currentOrNull(), LogType.INFO);

		ItineraryResponseDTO response = service.removeBookingFromItinerary(dto);

		log.info("Booking removed successfully from itineraryId: {}", dto.getItineraryId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Get all itineraries for a user")
	@GetMapping("/user/{userId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT','ADMIN')")
	public ResponseEntity<List<ItineraryResponseDTO>> getUserItineraries(@PathVariable Long userId) {

		log.info("Received request to fetch itineraries for userId: {}", userId);

		List<ItineraryResponseDTO> itineraries = service.getUserItineraries(userId);

		log.info("Total itineraries fetched for userId {}: {}", userId, itineraries.size());

		return new ResponseEntity<>(itineraries, HttpStatus.OK);
	}
	@Operation(summary = "Get an itinerary by ID")
	@GetMapping("/{itineraryId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT','ADMIN')")
	public ResponseEntity<ItineraryResponseDTO> getItineraryById(@PathVariable Long itineraryId,
			@RequestParam Long userId) {

		log.info("Received request to fetch itineraryId: {} for userId: {}", itineraryId, userId);

		ItineraryResponseDTO response = service.getItineraryById(itineraryId, userId);

		log.info("Itinerary fetched successfully: ID={}", itineraryId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Update an itinerary by ID")
	@PutMapping("/{itineraryId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<ItineraryResponseDTO> updateItinerary(@PathVariable Long itineraryId,
			@RequestBody @Valid CreateItineraryDTO dto) {

		log.info("Received request to update itineraryId: {}", itineraryId);
		auditLogService.logAction(AuditActions.UPDATE_ITINERARY, AuditEntity.ITINERARY, itineraryId, authUser.currentOrNull(), LogType.INFO);

		ItineraryResponseDTO response = service.updateItinerary(itineraryId, dto);

		log.info("Itinerary updated successfully with ID: {}", itineraryId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Operation(summary = "Delete an itinerary (detaches its bookings first)")
	@DeleteMapping("/{itineraryId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TRAVEL_AGENT')")
	public ResponseEntity<Void> deleteItinerary(@PathVariable Long itineraryId, @RequestParam Long userId) {

		log.info("Received request to delete itineraryId: {} for userId: {}", itineraryId, userId);
		auditLogService.logAction(AuditActions.DELETE_ITINERARY, AuditEntity.ITINERARY, itineraryId, authUser.currentOrNull(), LogType.WARN);

		service.deleteItinerary(itineraryId, userId);

		log.info("Itinerary deleted successfully with ID: {}", itineraryId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
