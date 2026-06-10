package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.HotelDTO;
import com.cts.dto.HotelResponseDTO;
import com.cts.entity.Hotel;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.HotelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/hotels")
@AllArgsConstructor
@Validated
@Tag(name = "Hotel Controller", description = "Manage hotel inventory and search hotels by location, rating, or price range")
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @Operation(summary = "Add a new hotel to the inventory")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    public ResponseEntity<Hotel> addHotel(@RequestBody @Valid HotelDTO dto) {

        log.info("Received request to add hotel: {}", dto);
        auditLogService.logAction(AuditActions.CREATE_HOTEL, AuditEntity.HOTEL, null, authUser.currentOrNull(), LogType.INFO);

        Hotel hotel = hotelService.addHotel(dto);

        log.info("Hotel created successfully with ID: {}", hotel.getHotelId());

        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing hotel by ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAVEL_AGENT')")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id,
                                             @RequestBody @Valid HotelDTO dto) {

        log.info("Received request to update hotel with ID: {}", id);
        auditLogService.logAction(AuditActions.UPDATE_HOTEL, AuditEntity.HOTEL, id, authUser.currentOrNull(), LogType.INFO);

        Hotel updatedHotel = hotelService.updateHotel(id, dto);

        log.info("Hotel updated successfully with ID: {}", id);

        return new ResponseEntity<>(updatedHotel, HttpStatus.OK);
    }

    @Operation(summary = "Get hotels by city")
    @GetMapping("/city/{location}")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Fetching hotels for location '{}' (page={}, size={})",
                location, page, size);

        List<HotelResponseDTO> hotels = hotelService.findByLocation(location, page, size);

        log.info("Found {} hotels in location '{}'", hotels.size(), location);

        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    @Operation(summary = "Filter hotels by city, rating, and price range")
    @GetMapping("/filter")
    public ResponseEntity<?> filterHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer ratings,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        log.info("Filtering hotels with params: city={}, ratings={}, minPrice={}, maxPrice={}, page={}, size={}",
                city, ratings, minPrice, maxPrice, page, size);

        Object result = hotelService.getFilteredHotels(city, ratings, minPrice, maxPrice, page, size);

        if (result instanceof List<?>) {
            log.info("Filter returned {} hotels", ((List<?>) result).size());
        } 

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
