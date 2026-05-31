package com.cts.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;
import com.cts.service.HotelService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/hotels")
@AllArgsConstructor
@Validated
public class HotelController {

    private final HotelService hotelService;

  
    @PostMapping
    public ResponseEntity<Hotel> addHotel(@RequestBody @Valid HotelDTO dto) {

        Hotel hotel = hotelService.addHotel(dto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id,
                                             @RequestBody @Valid HotelDTO dto) {

        return new ResponseEntity<>(hotelService.updateHotel(id, dto), HttpStatus.OK);
    }

    
    @GetMapping("/city/{location}")
    public ResponseEntity<List<Hotel>> getHotelsByLocation(@PathVariable String location,@RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {
    	
       List<Hotel> hotels = hotelService.findByLocation(location,page,size);
        return new ResponseEntity<>(hotels,HttpStatus.OK);
    }

    
    @GetMapping("/filter")
    public ResponseEntity<?> filterHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer ratings,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

        return new ResponseEntity<>(
                hotelService.getFilteredHotels(city, ratings, minPrice, maxPrice,page,size),
                HttpStatus.OK
        );
    }
}