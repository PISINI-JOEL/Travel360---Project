package com.cts.serviceimpl;

import org.springframework.stereotype.Service;

import com.cts.repository.TransportRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.FlightService;
import com.cts.service.HotelService;
import com.cts.service.SearchService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final FlightService flightService;
    private final HotelService hotelService;
    private final TravelPackageRepository packageRepo;
    private final TransportRepository transportRepo;

    @Override
    public Object search(String type,
                         String source,
                         String destination,
                         String city,
                         Double min,
                         Double max,
                         Integer ratings,
                         int page,
                         int size) {

        switch (type.toLowerCase()) {

            case "flight":
                validateFlight(source, destination);
                return flightService.filterFlights(source, destination, min, max, page, size);
                // ✅ returns List<Flight>

            case "hotel":
                validateHotel(city);
                return hotelService.getFilteredHotels(city, ratings, min, max);
                // ✅ returns List<Hotel>

            case "package":
                return packageRepo.findAll();
                // ✅ List

            case "transport":
                return transportRepo.findAll();
                // ✅ List

            default:
                throw new IllegalArgumentException("Invalid search type");
        }
    }

    private void validateFlight(String source, String destination) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Source and Destination are required");
        }
    }

    private void validateHotel(String city) {
        if (city == null) {
            throw new IllegalArgumentException("City is required");
        }
    }
}
