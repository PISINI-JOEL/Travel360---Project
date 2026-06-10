package com.cts.service;

import com.cts.dto.FlightDTO;
import com.cts.dto.FlightResponseDTO;
import com.cts.entity.Flight;

import java.util.List;

public interface FlightService {

    Flight addFlight(FlightDTO dto);

    Flight updateFlight(Long id, FlightDTO dto);

    List<FlightResponseDTO> searchFlights(String source, String destination, int page, int size);

    List<FlightResponseDTO> getAllFlights(int page, int size);

    List<FlightResponseDTO> filterFlights(String source, String destination,
                               Double min, Double max,
                               int page, int size);

    FlightResponseDTO getFlightById(Long id);
}
