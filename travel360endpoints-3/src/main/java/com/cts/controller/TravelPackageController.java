package com.cts.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.*;
import com.cts.enums.TravelPackageCategory;
import com.cts.service.TravelPackageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/packages")
@AllArgsConstructor
@Validated
public class TravelPackageController {

    private final TravelPackageService service;

    @PostMapping
    public ResponseEntity<TravelPackageResponseDTO> addPackage(
            @RequestBody @Valid TravelPackageDTO dto) {

        return new ResponseEntity<>(service.addPackage(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelPackageResponseDTO> updatePackage(
            @PathVariable Long id,
            @RequestBody @Valid TravelPackageDTO dto) {

        return new ResponseEntity<>(service.updatePackage(id, dto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        List<TravelPackageResponseDTO> list =
                service.getAllPackages(page, size);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getByCategory(
            @PathVariable TravelPackageCategory category,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        List<TravelPackageResponseDTO> list =
                service.searchByCategory(category, page, size);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
