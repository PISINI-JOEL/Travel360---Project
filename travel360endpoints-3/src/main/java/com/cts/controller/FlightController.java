package com.cts.controller;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.service.FlightService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@AllArgsConstructor
@Validated
@Slf4j
public class FlightController {

    private final FlightService service;

    @PostMapping
    public ResponseEntity<Flight> addFlight(@RequestBody @Valid FlightDTO dto) {

        log.info("Received request to add flight: {}", dto);

        Flight flight = service.addFlight(dto);

        log.info("Flight created successfully with ID: {}", flight.getFlightId());

        return new ResponseEntity<>(flight, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id,
                                               @RequestBody @Valid FlightDTO dto) {

        log.info("Received request to update flight with ID: {}", id);

        Flight updatedFlight = service.updateFlight(id, dto);

        log.info("Flight updated successfully with ID: {}", id);

        return new ResponseEntity<>(updatedFlight, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> search(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Searching flights from '{}' to '{}' (page={}, size={})",
                source, destination, page, size);

        List<Flight> flights = service.searchFlights(source, destination, page, size);

        log.info("Found {} flights for search query", flights.size());

        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Fetching all flights (page={}, size={})", page, size);

        List<Flight> flights = service.getAllFlights(page, size);

        log.info("Total flights fetched: {}", flights.size());

        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Flight>> filterFlights(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Filtering flights from '{}' to '{}' with price range min={}, max={}, page={}, size={}",
                source, destination, min, max, page, size);

        List<Flight> flights = service.filterFlights(source, destination, min, max, page, size);

        log.info("Filtered results count: {}", flights.size());

        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getById(@PathVariable Long id) {

        log.info("Fetching flight with ID: {}", id);

        Flight flight = service.getFlightById(id);

        log.info("Flight fetched successfully: ID={}", id);

        return new ResponseEntity<>(flight, HttpStatus.OK);
    }
}