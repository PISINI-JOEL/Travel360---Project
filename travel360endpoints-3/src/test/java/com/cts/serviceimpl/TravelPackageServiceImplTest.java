package com.cts.serviceimpl;

import com.cts.dto.TravelPackageDTO;
import com.cts.entity.Partner;
import com.cts.entity.TravelPackage;
import com.cts.enums.*;
import com.cts.exception.*;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class TravelPackageServiceImplTest {

    @Mock
    private TravelPackageRepository packageRepo;

    @Mock
    private PartnerRepository partnerRepo;

    @Mock
    private AuthenticatedUserProvider authUser;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TravelPackageServiceImpl service;

    private TravelPackageDTO dto;
    private Partner partner;

    @BeforeEach
    void setup() {

        dto = new TravelPackageDTO();
        dto.setPackageName("Goa Trip");
        dto.setSource("Chennai");
        dto.setDestination("Goa");
        dto.setPrice(10000.0);
        dto.setDurationDays(5);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(6));
        dto.setTotalSlots(20);
        dto.setDescription("Holiday package");
        dto.setCategory(TravelPackageCategory.FAMILY);
        dto.setStatus(PackageStatus.AVAILABLE);
        dto.setPartnerId(1L);

        partner = new Partner();
        partner.setPartnerId(1L);
        partner.setType(PartnerType.PACKAGE);
        partner.setStatus(PartnerStatus.ACTIVE);
    }

    // ✅ ADD SUCCESS
    @Test
    void addPackage_success() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(packageRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        assertNotNull(service.addPackage(dto));
    }

    // ✅ ADD FAILS
    @Test
    void addPackage_partnerNotFound() {

        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.addPackage(dto));
    }

    @Test
    void addPackage_invalidPartnerType() {

        partner.setType(PartnerType.HOTEL);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.addPackage(dto));
    }

    @Test
    void addPackage_inactivePartner() {

        partner.setStatus(PartnerStatus.INACTIVE);

        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.addPackage(dto));
    }

    // ✅ UPDATE SUCCESS
    @Test
    void updatePackage_success() {

        TravelPackage pkg = new TravelPackage();

        when(packageRepo.findById(1L)).thenReturn(Optional.of(pkg));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));
        when(packageRepo.save(any())).thenReturn(pkg);

        assertNotNull(service.updatePackage(1L, dto));
    }

    // ✅ UPDATE FAILS
    @Test
    void updatePackage_notFound() {

        when(packageRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PackageNotFoundException.class,
                () -> service.updatePackage(1L, dto));
    }

    @Test
    void updatePackage_partnerNotFound() {

        when(packageRepo.findById(1L)).thenReturn(Optional.of(new TravelPackage()));
        when(partnerRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> service.updatePackage(1L, dto));
    }

    @Test
    void updatePackage_invalidPartnerType() {

        partner.setType(PartnerType.HOTEL);

        when(packageRepo.findById(1L)).thenReturn(Optional.of(new TravelPackage()));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.updatePackage(1L, dto));
    }

    @Test
    void updatePackage_inactivePartner() {

        partner.setStatus(PartnerStatus.INACTIVE);

        when(packageRepo.findById(1L)).thenReturn(Optional.of(new TravelPackage()));
        when(partnerRepo.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> service.updatePackage(1L, dto));
    }

    // ✅ GET ALL
    @Test
    void getAllPackages() {

        when(packageRepo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new TravelPackage())));

        assertFalse(service.getAllPackages(0, 5).isEmpty());
    }

    // ✅ CATEGORY SEARCH
    @Test
    void searchByCategory() {

        when(packageRepo.findByCategory(any(), any(Pageable.class)))
                .thenReturn(new PageImpl(List.of(new TravelPackage())));

        assertFalse(service.searchByCategory(TravelPackageCategory.FAMILY, 0, 5).isEmpty());
    }
}