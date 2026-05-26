package com.cts.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.repository.TransportRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.FlightService;
import com.cts.service.HotelService;

import lombok.AllArgsConstructor;
@RestController
@RequestMapping("/api/v1/search")
@AllArgsConstructor
public class SearchController {

    private final FlightService flightService;
    private final HotelService hotelService;
    private final TravelPackageRepository packageRepo;
    private final TransportRepository transportRepo;

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam String type,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) Integer ratings) {

        switch (type.toLowerCase()) {

            case "flight":

                
                if (source == null || destination == null) {
                    return ResponseEntity.badRequest()
                            .body("Source and Destination are required for flight search");
                }

                return ResponseEntity.ok(
                        flightService.filterFlights(source, destination, min, max)
                );

            case "hotel":

                if (city == null) {
                    return ResponseEntity.badRequest()
                            .body("City is required for hotel search");
                }

                return ResponseEntity.ok(
                        hotelService.getFilteredHotels(city, ratings, min, max)
                );

            case "package":

                return ResponseEntity.ok(packageRepo.findAll());

            case "transport":

                return ResponseEntity.ok(transportRepo.findAll());

            default:
                return ResponseEntity.badRequest().body("Invalid search type");
        }
    }
}


