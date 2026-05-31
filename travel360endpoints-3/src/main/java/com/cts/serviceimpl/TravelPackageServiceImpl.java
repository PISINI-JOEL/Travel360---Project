package com.cts.serviceimpl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.dto.*;
import com.cts.entity.Partner;
import com.cts.entity.TravelPackage;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.enums.TravelPackageCategory;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PackageNotFoundException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.TravelPackageService;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TravelPackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository packageRepo;
    private final PartnerRepository partnerRepo;

    @Override
    public TravelPackageResponseDTO addPackage(TravelPackageDTO dto) {

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.PACKAGE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a PACKAGE partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
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

        return mapToDTO(tpackage);
    }

    @Override
    @Transactional
    public TravelPackageResponseDTO updatePackage(Long id, TravelPackageDTO dto) {

        TravelPackage tpackage = packageRepo.findById(id)
                .orElseThrow(() -> new PackageNotFoundException("Package not found"));

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.PACKAGE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a PACKAGE partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

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

        return mapToDTO(tpackage);
    }

    @Override
    public List<TravelPackageResponseDTO> getAllPackages(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return packageRepo.findAll(pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<TravelPackageResponseDTO> searchByCategory(TravelPackageCategory category, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return packageRepo.findByCategory(category, pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
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
