package com.cts.serviceimpl;

import org.springframework.stereotype.Service;

import com.cts.enums.TravelPackageCategory;
import com.cts.service.FlightService;
import com.cts.service.HotelService;
import com.cts.service.SearchService;
import com.cts.service.TransportService;
import com.cts.service.TravelPackageService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final FlightService flightService;
    private final HotelService hotelService;
    private final TravelPackageService packageService;
    private final TransportService transportService;

    @Override
    public Object search(String type,
                         String source,
                         String destination,
                         String city,
                         Double min,
                         Double max,
                         Integer ratings,
                         TravelPackageCategory category,
                         int page,
                         int size) {

        log.info("Performing search of type '{}' (page={}, size={})", type, page, size);

        switch (type.toLowerCase()) {

            case "flight":
                log.debug("Routing search to flight service");
                validateFlight(source, destination);
                return flightService.filterFlights(source, destination, min, max, page, size);
                // ✅ returns List<FlightResponseDTO>

            case "hotel":
                log.debug("Routing search to hotel service");
                validateHotel(city);
                return hotelService.getFilteredHotels(city, ratings, min, max,page,size);
                // ✅ returns List<HotelResponseDTO>

            case "package":
                log.debug("Routing search to package service");
                // category is optional: filter by it when supplied, otherwise return all
                if (category != null) {
                    return packageService.searchByCategory(category, page, size);
                }
                return packageService.getAllPackages(page, size);
                // ✅ List

            case "transport":
                log.debug("Routing search to transport service");
                validateTransport(source, destination);
                return transportService.findByRoute(source, destination, page, size);
                // ✅ List

            default:
                log.error("Invalid search type requested: {}", type);
                throw new IllegalArgumentException("Invalid search type");
        }
    }

    private void validateFlight(String source, String destination) {
        if (source == null || destination == null) {
            log.error("Flight search validation failed: source or destination is null");
            throw new IllegalArgumentException("Source and Destination are required");
        }
    }

    private void validateHotel(String city) {
        if (city == null) {
            log.error("Hotel search validation failed: city is null");
            throw new IllegalArgumentException("City is required");
        }
    }

    private void validateTransport(String source, String destination) {
        if (source == null || destination == null) {
            log.error("Transport search validation failed: source or destination is null");
            throw new IllegalArgumentException("Source and Destination are required");
        }
    }
}
