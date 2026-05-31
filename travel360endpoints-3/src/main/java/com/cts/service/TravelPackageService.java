package com.cts.service;

import java.util.List;

import com.cts.dto.TravelPackageDTO;
import com.cts.dto.TravelPackageResponseDTO;
import com.cts.enums.TravelPackageCategory;

public interface TravelPackageService {

    TravelPackageResponseDTO addPackage(TravelPackageDTO dto);

    TravelPackageResponseDTO updatePackage(Long id, TravelPackageDTO dto);

    List<TravelPackageResponseDTO> getAllPackages(int page, int size);

    List<TravelPackageResponseDTO> searchByCategory(TravelPackageCategory category, int page, int size);
}

