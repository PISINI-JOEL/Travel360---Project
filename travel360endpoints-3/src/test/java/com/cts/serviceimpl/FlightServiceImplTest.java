package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import com.cts.dto.FlightDTO;
import com.cts.entity.Flight;
import com.cts.entity.Partner;
import com.cts.enums.*;
import com.cts.exception.*;
import com.cts.repository.FlightRepository;
import com.cts.repository.PartnerRepository;

class FlightServiceImplTest {

    @InjectMocks
    private FlightServiceImpl service;

    @Mock
    private FlightRepository repo;

    @Mock
    private PartnerRepository partnerRepo;

    private FlightDTO dto;
    private Partner partner;
    private Flight flight;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        dto = new FlightDTO();
        dto.setPartnerId(1L);
        dto.setFlightNumber("AA-123");
        dto.setSource("Chennai");
        dto.setDestination("Delhi");
        dto.setArrivalTime(LocalTime.of(10, 0));
        dto.setDepartureTime(LocalTime.of(8, 0));
        dto.setTotalSeats(100);
        dto.setPrice(5000.0);
        dto.setStatus(FlightStatus.SCHEDULED);

        partner = new Partner();
        partner.setPartnerId(1L);
        partner.setName("Indigo");
        partner.setType(PartnerType.FLIGHT);
        partner.setStatus(PartnerStatus.ACTIVE);

        flight = Flight.builder()
                .flightId(10L)
                .build();
    }

    // ✅ ADD SUCCESS
    @Test
    void testAddFlight() {
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(repo.save(any())).thenReturn(flight);

        Flight result = service.addFlight(dto);

        assertNotNull(result);
        verify(repo).save(any());
    }

    // ✅ ADD - PARTNER NOT FOUND
    @Test
    void testAddFlight_PartnerNotFound() {
        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.addFlight(dto));
    }

    // ✅ ADD - INVALID TYPE
    @Test
    void testAddFlight_InvalidType() {
        partner.setType(PartnerType.HOTEL);
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.addFlight(dto));
    }

    // ✅ ADD - INACTIVE PARTNER
    @Test
    void testAddFlight_InactivePartner() {
        partner.setStatus(PartnerStatus.INACTIVE);
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.addFlight(dto));
    }

    // ✅ UPDATE SUCCESS
    @Test
    void testUpdateFlight() {
        when(repo.findById(10L)).thenReturn(Optional.of(flight));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(repo.save(any())).thenReturn(flight);

        Flight result = service.updateFlight(10L, dto);

        assertNotNull(result);
    }

    // ✅ UPDATE - FLIGHT NOT FOUND
    @Test
    void testUpdateFlight_FlightNotFound() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class,
                () -> service.updateFlight(10L, dto));
    }

    // ✅ UPDATE - PARTNER NOT FOUND
    @Test
    void testUpdateFlight_PartnerNotFound() {
        when(repo.findById(10L)).thenReturn(Optional.of(flight));
        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.updateFlight(10L, dto));
    }

    // ✅ UPDATE - INVALID TYPE
    @Test
    void testUpdateFlight_InvalidType() {
        partner.setType(PartnerType.HOTEL);

        when(repo.findById(10L)).thenReturn(Optional.of(flight));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.updateFlight(10L, dto));
    }

    // ✅ UPDATE - INACTIVE PARTNER
    @Test
    void testUpdateFlight_InactivePartner() {
        partner.setStatus(PartnerStatus.INACTIVE);

        when(repo.findById(10L)).thenReturn(Optional.of(flight));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.updateFlight(10L, dto));
    }

    // ✅ SEARCH
    @Test
    void testSearchFlights() {
        Page<Flight> page = new PageImpl<>(List.of(flight));

        when(repo.findBySourceAndDestination(
                eq("Chennai"), eq("Delhi"), any(Pageable.class)))
                .thenReturn(page);

        List<Flight> result = service.searchFlights("Chennai", "Delhi", 0, 5);

        assertEquals(1, result.size());
    }

    // ✅ GET ALL (Fix for ambiguity)
    @Test
    void testGetAllFlights() {
        Page<Flight> page = new PageImpl<>(List.of(flight));

        when(repo.findAll(any(Pageable.class))).thenReturn(page);

        List<Flight> result = service.getAllFlights(0, 5);

        assertEquals(1, result.size());
    }

    // ✅ FILTER WITH PRICE
    @Test
    void testFilterFlights_WithPrice() {
        Page<Flight> page = new PageImpl<>(List.of(flight));

        when(repo.findBySourceAndDestinationAndPriceBetween(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        List<Flight> result =
                service.filterFlights("Chennai", "Delhi", 1000.0, 5000.0, 0, 5);

        assertFalse(result.isEmpty());
    }

    // ✅ FILTER WITHOUT PRICE
    @Test
    void testFilterFlights_NoPrice() {
        Page<Flight> page = new PageImpl<>(List.of(flight));

        when(repo.findBySourceAndDestination(
                any(), any(), any(Pageable.class)))
                .thenReturn(page);

        List<Flight> result =
                service.filterFlights("Chennai", "Delhi", null, null, 0, 5);

        assertFalse(result.isEmpty());
    }

    // ✅ GET BY ID SUCCESS
    @Test
    void testGetFlightById() {
        when(repo.findById(10L)).thenReturn(Optional.of(flight));

        Flight result = service.getFlightById(10L);

        assertNotNull(result);
    }

    // ✅ GET BY ID NOT FOUND
    @Test
    void testGetFlightById_NotFound() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class,
                () -> service.getFlightById(10L));
    }
}