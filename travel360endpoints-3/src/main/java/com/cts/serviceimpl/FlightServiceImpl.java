package com.cts.serviceimpl;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.entity.Partner;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.exception.FlightNotFoundException;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.repository.FlightRepository;
import com.cts.repository.PartnerRepository;
import com.cts.service.AuditLogService;
import com.cts.service.FlightService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private final FlightRepository repo;
    private final PartnerRepository partnerRepo;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserProvider authUser;

    @Override
    public Flight addFlight(FlightDTO dto) {

        log.info("Adding new flight with partnerId: {}", dto.getPartnerId());

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.FLIGHT) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a FLIGHT partner");
        }

        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
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
                .totalSeats(dto.getTotalSeats())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .partner(partner)
                .build();

        Flight savedFlight = repo.save(flight);
        auditLogService.logAction(
                AuditActions.CREATE_FLIGHT,
                AuditEntity.FLIGHT,
                savedFlight.getFlightId(),
                authUser.currentOrNull(),
                LogType.INFO);
        	

        log.info("Flight created successfully with ID: {}", savedFlight.getFlightId());

        return savedFlight;
    }

    @Override
    @Transactional
    public Flight updateFlight(Long id, FlightDTO dto) {

        log.info("Updating flight with ID: {}", id);

        Flight flight = repo.findById(id)
                .orElseThrow(() -> {
                    log.error("Flight not found with id {}", id);
                    return new FlightNotFoundException("Flight not found");
                });

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.FLIGHT) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a FLIGHT partner");
        }

        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        log.debug("Updating flight details for ID: {}", id);

        flight.setFlightNumber(dto.getFlightNumber());
        flight.setAirlineName(partner.getName());
        flight.setSource(dto.getSource());
        flight.setDestination(dto.getDestination());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setTotalSeats(dto.getTotalSeats());
        flight.setPrice(dto.getPrice());
        flight.setStatus(dto.getStatus());
        flight.setPartner(partner);

        Flight updatedFlight = repo.save(flight);
        auditLogService.logAction(
                AuditActions.UPDATE_FLIGHT,
                AuditEntity.FLIGHT,
                updatedFlight.getFlightId(),
                authUser.currentOrNull(),
                LogType.INFO);

        log.info("Flight updated successfully with ID: {}", id);

        return updatedFlight;
    }

    @Override
    public List<Flight> searchFlights(String source, String destination, int page, int size) {

        log.info("Searching flights from '{}' to '{}' (page={}, size={})",
                source, destination, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightPage =
                repo.findBySourceAndDestination(source, destination, pageable);

        log.info("Search returned {} flights", flightPage.getContent().size());

        return flightPage.getContent();
    }

    @Override
    public List<Flight> getAllFlights(int page, int size) {

        log.info("Fetching all flights (page={}, size={})", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightPage = repo.findAll(pageable);

        log.info("Total flights fetched: {}", flightPage.getContent().size());

        return flightPage.getContent();
    }

    @Override
    public List<Flight> filterFlights(String source, String destination,
                                      Double min, Double max,
                                      int page, int size) {

        log.info("Filtering flights from '{}' to '{}' with minPrice={}, maxPrice={}",
                source, destination, min, max);

        Pageable pageable = PageRequest.of(page, size);

        Page<Flight> flightPage;

        if (min != null && max != null) {
            log.debug("Applying price filter between {} and {}", min, max);

            flightPage = repo.findBySourceAndDestinationAndPriceBetween(
                    source, destination, min, max, pageable);
        } else {
            log.debug("No price filter applied");

            flightPage = repo.findBySourceAndDestination(source, destination, pageable);
        }

        log.info("Filter returned {} flights", flightPage.getContent().size());

        return flightPage.getContent();
    }

    @Override
    public Flight getFlightById(Long id) {

        log.info("Fetching flight with ID: {}", id);

        return repo.findById(id)
                .orElseThrow(() -> {
                    log.error("Flight not found with id {}", id);
                    return new FlightNotFoundException("Flight not found");
                });
    }
}