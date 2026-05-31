package com.cts.serviceimpl;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.entity.Partner;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.exception.FlightNotFoundException;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.FlightRepository;
import com.cts.repository.PartnerRepository;
import com.cts.service.FlightService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository repo;
    private final PartnerRepository partnerRepo;

    @Override
    public Flight addFlight(FlightDTO dto) {

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.FLIGHT) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a FLIGHT partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        Flight flight = Flight.builder()
                .flightNumber(dto.getFlightNumber())
                .airlineName(partner.getName())
                .source(dto.getSource())
                .destination(dto.getDestination())
                .arrivalTime(dto.getArrivalTime())
                .departureTime(dto.getDepartureTime())
                .flightDate(dto.getFlightDate())
                .totalSeats(dto.getTotalSeats())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .partner(partner)
                .build();

        return repo.save(flight);
    }

    @Override
    @Transactional
    public Flight updateFlight(Long id, FlightDTO dto) {

        Flight flight = repo.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight not found"));

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.FLIGHT) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a FLIGHT partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        flight.setFlightNumber(dto.getFlightNumber());
        flight.setAirlineName(partner.getName());
        flight.setSource(dto.getSource());
        flight.setDestination(dto.getDestination());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setFlightDate(dto.getFlightDate());
        flight.setTotalSeats(dto.getTotalSeats());
        flight.setPrice(dto.getPrice());
        flight.setStatus(dto.getStatus());
        flight.setPartner(partner);

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
                .orElseThrow(() -> new FlightNotFoundException("Flight not found"));
    }
}
