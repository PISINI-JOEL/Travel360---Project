package com.cts.controller;

import com.cts.dto.PackageItineraryRequestDTO;
import com.cts.dto.PackageItineraryResponceDTO;
import com.cts.entity.PackageItinerary;
import com.cts.service.PackageItineraryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/package/itineraries")
@Tag(name="Package Itinerary (add ,delete & check )",description ="Operations related to package Itinerary")
public class PackageItineraryController {

    private final PackageItineraryService service;

    public PackageItineraryController(PackageItineraryService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary="Add itinerary to an existing package",
               description="Posts only itinerary details with reference to packageId")
    public ResponseEntity<PackageItinerary> create(@RequestBody PackageItineraryRequestDTO dto) {
        PackageItinerary saved = service.save(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @Operation(summary="Displays all the itinerary ",
	   description="Displayed all the itinerary")
    public List<PackageItinerary> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary="Displays the itinerary by id ",
	   description="Displayed the itinerary by id")
    public ResponseEntity<PackageItineraryResponceDTO> getItineraryById(@PathVariable Long id) {
        PackageItineraryResponceDTO dto = service.getItineraryById(id); 
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @Operation(summary="Delete the itinerary by id ",
	   description="Deleted the itinerary by id")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "Deleted successfully";
    }
}
