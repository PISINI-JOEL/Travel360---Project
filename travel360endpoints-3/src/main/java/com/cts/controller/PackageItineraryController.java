package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.PackageItineraryRequestDTO;
import com.cts.dto.PackageItineraryResponceDTO;
import com.cts.entity.PackageItinerary;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.PackageItineraryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/package/itineraries")
@Tag(name="Package Itinerary (add ,delete & check )",description ="Operations related to package Itinerary")
@Slf4j
public class PackageItineraryController {

    private final PackageItineraryService service;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    public PackageItineraryController(PackageItineraryService service,
                                      AuthenticatedUserProvider authUser,
                                      AuditLogService auditLogService) {
        this.service = service;
        this.authUser = authUser;
        this.auditLogService = auditLogService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    @Operation(summary="Add itinerary to an existing package",
               description="Posts only itinerary details with reference to packageId")
    public ResponseEntity<PackageItinerary> create(@RequestBody PackageItineraryRequestDTO dto) {
        log.info("Received request to create package itinerary for packageId: {}", dto.getPackageId());
        auditLogService.logAction(AuditActions.CREATE_PACKAGE_ITINERARY, AuditEntity.ITINERARY, null, authUser.currentOrNull(), LogType.INFO);
        PackageItinerary saved = service.save(dto);
        log.info("Package itinerary created successfully with ID: {}", saved.getPackageItineraryId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @Operation(summary="Displays all the itinerary ",
	   description="Displayed all the itinerary")
    public List<PackageItinerary> getAll() {
        log.info("Received request to fetch all package itineraries");
        List<PackageItinerary> itineraries = service.getAll();
        log.info("Total package itineraries fetched: {}", itineraries.size());
        return itineraries;
    }

    @GetMapping("/{id}")
    @Operation(summary="Displays the itinerary by id ",
	   description="Displayed the itinerary by id")
    public ResponseEntity<PackageItineraryResponceDTO> getItineraryById(@PathVariable Long id) {
        log.info("Received request to fetch package itinerary with ID: {}", id);
        PackageItineraryResponceDTO dto = service.getItineraryById(id);
        log.info("Package itinerary fetched successfully: ID={}", id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    @Operation(summary="Delete the itinerary by id ",
	   description="Deleted the itinerary by id")
    public String delete(@PathVariable Long id) {
        log.info("Received request to delete package itinerary with ID: {}", id);
        auditLogService.logAction(AuditActions.DELETE_PACKAGE_ITINERARY, AuditEntity.ITINERARY, id, authUser.currentOrNull(), LogType.WARN);
        service.delete(id);
        log.info("Package itinerary deleted successfully with ID: {}", id);
        return "Deleted successfully";
    }
}
