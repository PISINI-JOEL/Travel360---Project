package com.cts.service;

import java.util.List;

import com.cts.dto.TravelPackageDTO;
import com.cts.dto.TravelPackageResponseDTO;
import com.cts.enums.TravelPackageCategory;

public interface TravelPackageService {

    TravelPackageResponseDTO addPackage(TravelPackageDTO dto);

    List<TravelPackageResponseDTO> getAllPackages();

    List<TravelPackageResponseDTO> searchByCategory(TravelPackageCategory category);
}

