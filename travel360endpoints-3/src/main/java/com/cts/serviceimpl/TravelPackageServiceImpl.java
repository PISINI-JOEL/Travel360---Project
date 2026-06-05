package com.cts.serviceimpl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.*;
import com.cts.entity.Partner;
import com.cts.entity.TravelPackage;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.enums.TravelPackageCategory;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PackageNotFoundException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.AuditLogService;
import com.cts.service.TravelPackageService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class TravelPackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository packageRepo;
    private final PartnerRepository partnerRepo;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @Override
    public TravelPackageResponseDTO addPackage(TravelPackageDTO dto) {

        log.info("Adding new travel package with partnerId: {}", dto.getPartnerId());

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.PACKAGE) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a PACKAGE partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        TravelPackage tpackage = TravelPackage.builder()
                .packageName(dto.getPackageName())
                .source(dto.getSource())
                .destination(dto.getDestination())
                .price(dto.getPrice())
                .durationDays(dto.getDurationDays())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .totalSlots(dto.getTotalSlots())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .status(dto.getStatus())
                .partner(partner)
                .build();

        packageRepo.save(tpackage);
        auditLogService.logAction(AuditActions.CREATE_PACKAGE, AuditEntity.TRAVELPACKAGE, tpackage.getPackageId(), authUser.currentOrNull(), LogType.INFO);

        log.info("Travel package created successfully with ID: {}", tpackage.getPackageId());

        return mapToDTO(tpackage);
    }

    @Override
    @Transactional
    public TravelPackageResponseDTO updatePackage(Long id, TravelPackageDTO dto) {

        log.info("Updating travel package with ID: {}", id);

        TravelPackage tpackage = packageRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Package not found with id {}", id);
                    return new PackageNotFoundException("Package not found");
                });

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.PACKAGE) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a PACKAGE partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        log.debug("Updating travel package details for ID: {}", id);

        tpackage.setPackageName(dto.getPackageName());
        tpackage.setSource(dto.getSource());
        tpackage.setDestination(dto.getDestination());
        tpackage.setPrice(dto.getPrice());
        tpackage.setDurationDays(dto.getDurationDays());
        tpackage.setStartDate(dto.getStartDate());
        tpackage.setEndDate(dto.getEndDate());
        tpackage.setTotalSlots(dto.getTotalSlots());
        tpackage.setDescription(dto.getDescription());
        tpackage.setCategory(dto.getCategory());
        tpackage.setStatus(dto.getStatus());
        tpackage.setPartner(partner);

        packageRepo.save(tpackage);
        auditLogService.logAction(AuditActions.UPDATE_PACKAGE, AuditEntity.TRAVELPACKAGE, tpackage.getPackageId(), authUser.currentOrNull(), LogType.INFO);

        log.info("Travel package updated successfully with ID: {}", id);

        return mapToDTO(tpackage);
    }

    @Override
    public List<TravelPackageResponseDTO> getAllPackages(int page, int size) {

        log.info("Fetching all travel packages (page={}, size={})", page, size);

        Pageable pageable = PageRequest.of(page, size);

        List<TravelPackageResponseDTO> packages = packageRepo.findAll(pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();

        log.info("Total travel packages fetched: {}", packages.size());

        return packages;
    }

    @Override
    public List<TravelPackageResponseDTO> searchByCategory(TravelPackageCategory category, int page, int size) {

        log.info("Searching travel packages by category: {} (page={}, size={})", category, page, size);

        Pageable pageable = PageRequest.of(page, size);

        List<TravelPackageResponseDTO> packages = packageRepo.findByCategory(category, pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();

        log.info("Category search returned {} travel packages", packages.size());

        return packages;
    }

    private TravelPackageResponseDTO mapToDTO(TravelPackage t) {

        return TravelPackageResponseDTO.builder()
                .packageId(t.getPackageId())
                .packageName(t.getPackageName())
                .source(t.getSource())
                .destination(t.getDestination())
                .price(t.getPrice())
                .durationDays(t.getDurationDays())
                .startDate(t.getStartDate())
                .endDate(t.getEndDate())
                .totalSlots(t.getTotalSlots())
                .description(t.getDescription())
                .category(t.getCategory())
                .status(t.getStatus())
                .build();
    }
}
