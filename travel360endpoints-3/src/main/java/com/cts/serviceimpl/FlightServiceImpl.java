package com.cts.serviceimpl;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.repository.FlightRepository;
import com.cts.service.FlightService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository repo;

    @Override
    public Flight addFlight(FlightDTO dto) {

        Flight flight = Flight.builder()
                .flightNumber(dto.getFlightNumber())
                .airlineName(dto.getAirlineName())
                .source(dto.getSource())
                .destination(dto.getDestination())
                .arrivalTime(dto.getArrivalTime())
                .departureTime(dto.getDepartureTime())
                .flightDate(dto.getFlightDate())
                .totalSeats(dto.getTotalSeats())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .build();

        return repo.save(flight);
    }

    @Override
    public List<Flight> searchFlights(String source, String destination, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightPage =
                repo.findBySourceAndDestination(source, destination, pageable);

        return flightPage.getContent();
    }

    @Override
    public List<Flight> getAllFlights(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightPage = repo.findAll(pageable);

        return flightPage.getContent(); 
    }

    @Override
    public List<Flight> filterFlights(String source, String destination,
                                      Double min, Double max,
                                      int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightPage;

        if (min != null && max != null) {
            flightPage = repo.findBySourceAndDestinationAndPriceBetween(
                    source, destination, min, max, pageable);
        } else {
            flightPage = repo.findBySourceAndDestination(source, destination, pageable);
        }

        return flightPage.getContent(); 
    }

    @Override
    public Flight getFlightById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }
}
