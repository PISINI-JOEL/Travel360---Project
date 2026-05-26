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
                .destination(dto.getDestination())
                .price(dto.getPrice())
                .durationDays(dto.getDurationDays())
                .category(dto.getCategory())
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
                .destination(t.getDestination())
                .price(t.getPrice())
                .durationDays(t.getDurationDays())
                .category(t.getCategory())
                .build();
    }
}