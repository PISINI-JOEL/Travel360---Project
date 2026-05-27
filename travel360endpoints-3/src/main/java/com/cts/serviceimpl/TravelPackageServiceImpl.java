package com.cts.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.dto.*;
import com.cts.entity.TravelPackage;
import com.cts.enums.TravelPackageCategory;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.TravelPackageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TravelPackageServiceImpl implements TravelPackageService {

    private final TravelPackageRepository packageRepo;

    @Override
    public TravelPackageResponseDTO addPackage(TravelPackageDTO dto) {

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
                .build();

        packageRepo.save(tpackage);

        return mapToDTO(tpackage);
    }

    @Override
    public List<TravelPackageResponseDTO> getAllPackages() {

        return packageRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<TravelPackageResponseDTO> searchByCategory(TravelPackageCategory category) {

        return packageRepo.findByCategory(category)
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
