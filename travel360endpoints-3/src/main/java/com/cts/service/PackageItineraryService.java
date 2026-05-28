package com.cts.service;

import com.cts.dto.PackageItineraryRequestDTO;
import com.cts.dto.PackageItineraryResponceDTO;
import com.cts.entity.PackageItinerary;
import java.util.List;

public interface PackageItineraryService {
    
    List<PackageItinerary> getAll();
    PackageItineraryResponceDTO getItineraryById(Long id);
    void delete(Long id);
	PackageItinerary save(PackageItineraryRequestDTO dto);
}
