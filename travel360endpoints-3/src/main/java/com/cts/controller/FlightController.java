package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.FlightService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@AllArgsConstructor
@Validated
@Tag(name = "Flight Controller", description = "Manage flight inventory and search/filter available flights")
@Slf4j
public class FlightController {

    private final FlightService service;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @Operation(summary = "Add a new flight to the inventory")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    public ResponseEntity<Flight> addFlight(@RequestBody @Valid FlightDTO dto) {

        log.info("Received request to add flight: {}", dto);
        auditLogService.logAction(AuditActions.CREATE_FLIGHT, AuditEntity.FLIGHT, null, authUser.currentOrNull(), LogType.INFO);

        Flight flight = service.addFlight(dto);

        log.info("Flight created successfully with ID: {}", flight.getFlightId());

        return new ResponseEntity<>(flight, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing flight by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id,
                                               @RequestBody @Valid FlightDTO dto) {

        log.info("Received request to update flight with ID: {}", id);
        auditLogService.logAction(AuditActions.UPDATE_FLIGHT, AuditEntity.FLIGHT, id, authUser.currentOrNull(), LogType.INFO);

        Flight updatedFlight = service.updateFlight(id, dto);

        log.info("Flight updated successfully with ID: {}", id);

        return new ResponseEntity<>(updatedFlight, HttpStatus.OK);
    }

    @Operation(summary = "Search flights by source and destination")
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

    @Operation(summary = "Get all flights (paginated)")
    @GetMapping
    public ResponseEntity<List<Flight>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Fetching all flights (page={}, size={})", page, size);

        List<Flight> flights = service.getAllFlights(page, size);

        log.info("Total flights fetched: {}", flights.size());

        return new ResponseEntity<>(flights, HttpStatus.OK);
    }

    @Operation(summary = "Filter flights by route and price range")
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

    @Operation(summary = "Get a flight by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getById(@PathVariable Long id) {

        log.info("Fetching flight with ID: {}", id);

        Flight flight = service.getFlightById(id);

        log.info("Flight fetched successfully: ID={}", id);

        return new ResponseEntity<>(flight, HttpStatus.OK);
    }
}