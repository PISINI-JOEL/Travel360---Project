package com.cts.serviceimpl;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.repository.FlightRepository;
import com.cts.service.FlightService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {

	private final FlightRepository repo;

	public Flight addFlight(FlightDTO dto) {

		Flight flight = Flight.builder().flightNumber(dto.getFlightNumber()).airlineName(dto.getAirlineName())
				.source(dto.getSource()).destination(dto.getDestination()).arrivalTime(dto.getArrivalTime())
				.flightDate(dto.getFlightDate()).departureTime(dto.getDepartureTime()).arrivalTime(dto.getArrivalTime())

				.totalSeats(dto.getTotalSeats()).price(dto.getPrice()).status(dto.getStatus()).build();

		return repo.save(flight);
	}

	@Override
	public List<Flight> searchFlights(String source, String destination) {
		return repo.findBySourceAndDestination(source, destination);
	}

	@Override
	public List<Flight> getAllFlights() {
		return repo.findAll();
	}

	@Override
	public Flight getFlightById(Long id) {
		return repo.findById(id).orElse(null);
	}

	@Override
	public List<Flight> filterFlights(String source, String destination, Double min, Double max) {

		if (min != null && max != null) {
			return repo.findBySourceAndDestinationAndPriceBetween(source, destination, min, max);
		}

		return repo.findBySourceAndDestination(source, destination);
	}

}