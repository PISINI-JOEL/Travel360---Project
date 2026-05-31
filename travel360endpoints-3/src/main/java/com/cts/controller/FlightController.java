package com.cts.controller;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.service.FlightService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
@AllArgsConstructor
@Validated
public class FlightController {

    private final FlightService service;

    @PostMapping
    public ResponseEntity<Flight> addFlight(@RequestBody @Valid FlightDTO dto) {

        return new ResponseEntity<>(service.addFlight(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id,
                                               @RequestBody @Valid FlightDTO dto) {

        return new ResponseEntity<>(service.updateFlight(id, dto), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> search(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        return new ResponseEntity<>(
                service.searchFlights(source, destination, page, size),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        return new ResponseEntity<>(
                service.getAllFlights(page, size),
                HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Flight>> filterFlights(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        return new ResponseEntity<>(
                service.filterFlights(source, destination, min, max, page, size),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getById(@PathVariable Long id) {

        return new ResponseEntity<>(service.getFlightById(id), HttpStatus.OK);
    }
}