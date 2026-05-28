package com.cts.service;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;

import java.util.List;

public interface FlightService {

    Flight addFlight(FlightDTO dto);

    List<Flight> searchFlights(String source, String destination, int page, int size);

    List<Flight> getAllFlights(int page, int size);

    List<Flight> filterFlights(String source, String destination,
                               Double min, Double max,
                               int page, int size);

    Flight getFlightById(Long id);
}