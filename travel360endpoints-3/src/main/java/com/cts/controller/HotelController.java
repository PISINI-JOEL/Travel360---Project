package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;
import com.cts.service.HotelService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/hotels")
@AllArgsConstructor
public class HotelController {

    private final HotelService hotelService;

  
    @PostMapping
    public ResponseEntity<Hotel> addHotel(@RequestBody @Valid HotelDTO dto) {

        Hotel hotel = hotelService.addHotel(dto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    
    @GetMapping("/city/{location}")
    public ResponseEntity<List<Hotel>> getHotelsByLocation(@PathVariable String location) {

        List<Hotel> hotels = hotelService.findByLocation(location);
        return new ResponseEntity<>(hotels, HttpStatus.OK);
    }

    
    @GetMapping("/filter")
    public ResponseEntity<?> filterHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer ratings,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return new ResponseEntity<>(
                hotelService.getFilteredHotels(city, ratings, minPrice, maxPrice),
                HttpStatus.OK
        );
    }
}