package com.cts.serviceimpl;

import com.cts.dto.TransportDTO;
import com.cts.entity.Partner;
import com.cts.entity.Transport;
import com.cts.enums.*;
import com.cts.exception.*;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TransportRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransportServiceImplTest {

    @Mock
    private TransportRepository transportRepo;

    @Mock
    private PartnerRepository partnerRepo;

    @InjectMocks
    private TransportServiceImpl service;

    private TransportDTO dto;
    private Partner partner;

    @BeforeEach
    void setup() {

        dto = new TransportDTO();
        dto.setTransportNumber(123);
        dto.setSource("Chennai");
        dto.setDestination("Delhi");
        dto.setTransportType("BUS");
        dto.setDepartureTime(LocalTime.of(10, 0));
        dto.setArrivalTime(LocalTime.of(20, 0));
        dto.setTransportTotalSeats(50);
        dto.setPrice(1500.0);
        dto.setTransportStatus(TransportStatus.AVAILABLE);
        dto.setPartnerId(1L);

        partner = new Partner();
        partner.setPartnerId(1L);
        partner.setType(PartnerType.BUS);
        partner.setStatus(PartnerStatus.ACTIVE);
    }

    // ✅ ADD SUCCESS
    @Test
    void addTransport_success() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(transportRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        assertNotNull(service.addTransport(dto));
    }

    // ✅ ADD FAIL CASES
    @Test
    void addTransport_partnerNotFound() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.addTransport(dto));
    }

    @Test
    void addTransport_invalidPartnerType() {

        partner.setType(PartnerType.HOTEL);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.addTransport(dto));
    }

    @Test
    void addTransport_inactivePartner() {

        partner.setStatus(PartnerStatus.INACTIVE);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.addTransport(dto));
    }

    // ✅ UPDATE SUCCESS
    @Test
    void updateTransport_success() {

        Transport transport = new Transport();

        when(transportRepo.findById(1L)).thenReturn(Optional.of(transport));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(transportRepo.save(any())).thenReturn(transport);

        assertNotNull(service.updateTransport(1L, dto));
    }

    // ✅ UPDATE FAIL CASES
    @Test
    void updateTransport_notFound() {

        when(transportRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TransportNotFoundException.class,
                () -> service.updateTransport(1L, dto));
    }

    @Test
    void updateTransport_partnerNotFound() {

        when(transportRepo.findById(1L)).thenReturn(Optional.of(new Transport()));
        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.updateTransport(1L, dto));
    }

    @Test
    void updateTransport_invalidPartnerType() {

        partner.setType(PartnerType.HOTEL);

        when(transportRepo.findById(1L)).thenReturn(Optional.of(new Transport()));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.updateTransport(1L, dto));
    }

    @Test
    void updateTransport_inactivePartner() {

        partner.setStatus(PartnerStatus.INACTIVE);

        when(transportRepo.findById(1L)).thenReturn(Optional.of(new Transport()));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.updateTransport(1L, dto));
    }

    // ✅ GET ALL
    @Test
    void getAllTransports() {

        when(transportRepo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Transport())));

        assertFalse(service.getAllTransports(0, 5).isEmpty());
    }

    // ✅ ROUTE (FIXED MATCHERS)
    @Test
    void findByRoute() {

        when(transportRepo.findBySourceAndDestination(
                anyString(),
                anyString(),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(new Transport())));

        assertFalse(service.findByRoute("Chennai", "Delhi", 0, 5).isEmpty());
    }

    // ✅ STATUS (FIXED MATCHERS)
    @Test
    void findByStatus() {

        when(transportRepo.findByTransportStatus(
                any(TransportStatus.class),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(new Transport())));

        assertFalse(service.findByStatus(TransportStatus.AVAILABLE, 0, 5).isEmpty());
    }
}
