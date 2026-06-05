package com.cts.serviceimpl;

import com.cts.dto.PartnerDTO;
import com.cts.entity.*;
import com.cts.enums.*;
import com.cts.exception.PartnerNotFoundException;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.repository.*;
import com.cts.service.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceImplTest {

    @Mock private PartnerRepository partnerRepo;
    @Mock private FlightRepository flightRepo;
    @Mock private HotelRepository hotelRepo;
    @Mock private TransportRepository transportRepo;
    @Mock private TravelPackageRepository packageRepo;
    @Mock private AuthenticatedUserProvider authUser;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private PartnerServiceImpl service;

    private Partner partner;
    private PartnerDTO dto;

    @BeforeEach
    void setup() {

        partner = new Partner();
        partner.setPartnerId(1L);
        partner.setName("Test Partner");
        partner.setType(PartnerType.FLIGHT);
        partner.setStatus(PartnerStatus.ACTIVE);

        dto = new PartnerDTO();
        dto.setName("Updated Partner");
        dto.setType(PartnerType.FLIGHT);
        dto.setStatus(PartnerStatus.ACTIVE);
    }

    // ✅ CREATE
    @Test
    void createPartner() {

        when(partnerRepo.save(any())).thenReturn(partner);

        assertNotNull(service.createPartner(dto));
    }

    // ✅ GET BY ID SUCCESS
    @Test
    void getPartnerById_success() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertNotNull(service.getPartnerById(1L));
    }

    // ✅ GET BY ID FAIL
    @Test
    void getPartnerById_notFound() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.getPartnerById(1L));
    }

    // ✅ UPDATE ACTIVE
    @Test
    void updatePartner_active() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerRepo.save(any())).thenReturn(partner);

        assertNotNull(service.updatePartner(1L, dto));
    }

    // ✅ UPDATE INACTIVE → FLIGHT cascade
    @Test
    void updatePartner_inactive_flightCascade() {

        dto.setStatus(PartnerStatus.INACTIVE);
        partner.setType(PartnerType.FLIGHT);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerRepo.save(any())).thenReturn(partner);

        when(flightRepo.findByPartner(any()))
                .thenReturn(List.of(new Flight()));

        service.updatePartner(1L, dto);

        verify(flightRepo).saveAll(any());
    }

    // ✅ UPDATE INACTIVE → HOTEL cascade
    @Test
    void updatePartner_inactive_hotelCascade() {

        dto.setStatus(PartnerStatus.INACTIVE);

        // ✅ CRITICAL FIX
        partner.setType(PartnerType.HOTEL);
        dto.setType(PartnerType.HOTEL);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerRepo.save(any())).thenReturn(partner);

        when(hotelRepo.findByPartner(any()))
                .thenReturn(List.of(new Hotel()));

        service.updatePartner(1L, dto);

        verify(hotelRepo).saveAll(any());
    }
    // ✅ UPDATE INACTIVE → BUS cascade
    @Test
    void updatePartner_inactive_busCascade() {

        dto.setStatus(PartnerStatus.INACTIVE);
        partner.setType(PartnerType.BUS);
        dto.setType(PartnerType.BUS);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerRepo.save(any())).thenReturn(partner);

        when(transportRepo.findByPartner(any()))
                .thenReturn(List.of(new Transport()));

        service.updatePartner(1L, dto);

        verify(transportRepo).saveAll(any());
    }

    // ✅ UPDATE INACTIVE → PACKAGE cascade
    @Test
    void updatePartner_inactive_packageCascade() {

        dto.setStatus(PartnerStatus.INACTIVE);
        partner.setType(PartnerType.PACKAGE);
        dto.setType(PartnerType.PACKAGE);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerRepo.save(any())).thenReturn(partner);

        when(packageRepo.findByPartner(any()))
                .thenReturn(List.of(new TravelPackage()));

        service.updatePartner(1L, dto);

        verify(packageRepo).saveAll(any());
    }

    // ✅ UPDATE NOT FOUND
    @Test
    void updatePartner_notFound() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.updatePartner(1L, dto));
    }

    // ✅ DELETE SUCCESS (HOTEL CASE)
    @Test
    void deletePartner_success() {

        partner.setType(PartnerType.HOTEL);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(partnerRepo.save(any())).thenReturn(partner);

        when(hotelRepo.findByPartner(any()))
                .thenReturn(List.of(new Hotel()));

        service.deletePartner(1L);

        verify(hotelRepo).saveAll(any());
    }

    // ✅ DELETE NOT FOUND
    @Test
    void deletePartner_notFound() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.deletePartner(1L));
    }

    // ✅ CATEGORY
    @Test
    void getPartnerByCategory() {

        when(partnerRepo.findByType(any()))
                .thenReturn(List.of(partner));

        assertFalse(service.getPartnerByCategory(PartnerType.FLIGHT).isEmpty());
    }
}
