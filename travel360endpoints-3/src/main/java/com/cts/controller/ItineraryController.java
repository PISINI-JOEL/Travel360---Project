package com.cts.controller;

import com.cts.dto.AddBookingDTO;
import com.cts.dto.CreateItineraryDTO;
import com.cts.dto.ItineraryResponseDTO;
import com.cts.service.ItineraryService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@AllArgsConstructor
public class ItineraryController {

	private final ItineraryService service;

	@PostMapping
	public ResponseEntity<ItineraryResponseDTO> createItinerary(@RequestBody @Valid CreateItineraryDTO dto) {

		return new ResponseEntity<>(service.createItinerary(dto), HttpStatus.CREATED);
	}

	@PostMapping("/add-booking")
	public ResponseEntity<ItineraryResponseDTO> addBookingToItinerary(@RequestBody @Valid AddBookingDTO dto) {

		return new ResponseEntity<>(service.addBookingToItinerary(dto), HttpStatus.OK);
	}

	@PostMapping("/remove-booking")
	public ResponseEntity<ItineraryResponseDTO> removeBookingFromItinerary(@RequestBody @Valid AddBookingDTO dto) {

		return new ResponseEntity<>(service.removeBookingFromItinerary(dto), HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ItineraryResponseDTO>> getUserItineraries(@PathVariable Long userId) {

		return new ResponseEntity<>(service.getUserItineraries(userId), HttpStatus.OK);
	}

	@GetMapping("/{itineraryId}")
	public ResponseEntity<ItineraryResponseDTO> getItineraryById(@PathVariable Long itineraryId,
			@RequestParam Long userId) {

		return new ResponseEntity<>(service.getItineraryById(itineraryId, userId), HttpStatus.OK);
	}

	@PutMapping("/{itineraryId}")
	public ResponseEntity<ItineraryResponseDTO> updateItinerary(@PathVariable Long itineraryId,
			@RequestBody @Valid CreateItineraryDTO dto) {

		return new ResponseEntity<>(service.updateItinerary(itineraryId, dto), HttpStatus.OK);
	}

	@DeleteMapping("/{itineraryId}")
	public ResponseEntity<Void> deleteItinerary(@PathVariable Long itineraryId, @RequestParam Long userId) {

		service.deleteItinerary(itineraryId, userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
