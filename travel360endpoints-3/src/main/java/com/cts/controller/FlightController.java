package com.cts.controller;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.service.FlightService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@AllArgsConstructor
public class FlightController {

	private final FlightService service;

	@PostMapping
	public ResponseEntity<Flight> addFlight(@RequestBody @Valid FlightDTO dto) {

		return new ResponseEntity<>(service.addFlight(dto), HttpStatus.CREATED);
	}

	
	@GetMapping("/search")
	public ResponseEntity<List<Flight>> search(@RequestParam String source, @RequestParam String destination) {

		return new ResponseEntity<>(service.searchFlights(source, destination), HttpStatus.OK);
	}

	
	@GetMapping
	public ResponseEntity<List<Flight>> getAll() {

		return new ResponseEntity<>(service.getAllFlights(), HttpStatus.OK);
	}

	@GetMapping("/filter")
	public ResponseEntity<List<Flight>> filterFlights(@RequestParam String source, @RequestParam String destination,
			@RequestParam(required = false) Double min, @RequestParam(required = false) Double max) {

		return new ResponseEntity<>(service.filterFlights(source, destination, min, max), HttpStatus.OK);
	}

	
	@GetMapping("/{id}")
	public ResponseEntity<Flight> getById(@PathVariable Long id) {

		return new ResponseEntity<>(service.getFlightById(id), HttpStatus.OK);
	}
}
