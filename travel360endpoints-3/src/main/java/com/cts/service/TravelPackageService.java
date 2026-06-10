package com.cts.service;

import java.util.List;

import com.cts.dto.TravelPackageDTO;
import com.cts.dto.TravelPackageResponseDTO;
import com.cts.entity.TravelPackage;
import com.cts.enums.TravelPackageCategory;

public interface TravelPackageService {

    TravelPackage addPackage(TravelPackageDTO dto);

    TravelPackage updatePackage(Long id, TravelPackageDTO dto);

    List<TravelPackageResponseDTO> getAllPackages(int page, int size);

    List<TravelPackageResponseDTO> searchByCategory(TravelPackageCategory category, int page, int size);
}

