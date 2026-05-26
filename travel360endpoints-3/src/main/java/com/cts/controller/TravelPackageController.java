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
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/packages")
@AllArgsConstructor
public class TravelPackageController {

    private final TravelPackageService service;

    @PostMapping
    public ResponseEntity<TravelPackageResponseDTO> addPackage(
            @RequestBody @Valid TravelPackageDTO dto) {

        return new ResponseEntity<>(service.addPackage(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        List<TravelPackageResponseDTO> list =
                service.getAllPackages();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getByCategory(
            @PathVariable TravelPackageCategory category) {

        List<TravelPackageResponseDTO> list =
                service.searchByCategory(category);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}
