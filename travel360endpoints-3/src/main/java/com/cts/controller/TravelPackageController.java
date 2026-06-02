package com.cts.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.*;
import com.cts.enums.TravelPackageCategory;
import com.cts.service.TravelPackageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/packages")
@AllArgsConstructor
@Validated
@Slf4j
public class TravelPackageController {

    private final TravelPackageService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    public ResponseEntity<TravelPackageResponseDTO> addPackage(
            @RequestBody @Valid TravelPackageDTO dto) {

        log.info("Received request to add travel package");

        TravelPackageResponseDTO response = service.addPackage(dto);

        log.info("Travel package created successfully with ID: {}", response.getPackageId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    public ResponseEntity<TravelPackageResponseDTO> updatePackage(
            @PathVariable Long id,
            @RequestBody @Valid TravelPackageDTO dto) {

        log.info("Received request to update travel package with ID: {}", id);

        TravelPackageResponseDTO response = service.updatePackage(id, dto);

        log.info("Travel package updated successfully with ID: {}", id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Fetching all travel packages (page={}, size={})", page, size);

        List<TravelPackageResponseDTO> list =
                service.getAllPackages(page, size);

        log.info("Fetched {} travel packages", list.size());

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getByCategory(
            @PathVariable TravelPackageCategory category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Fetching travel packages by category={} (page={}, size={})",
                category, page, size);

        List<TravelPackageResponseDTO> list =
                service.searchByCategory(category, page, size);

        log.info("Found {} packages for category={}", list.size(), category);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
