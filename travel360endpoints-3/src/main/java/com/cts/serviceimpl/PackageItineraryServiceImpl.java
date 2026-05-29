package com.cts.serviceimpl;

import com.cts.dto.PackageItineraryRequestDTO;
import com.cts.dto.PackageItineraryResponceDTO;
import com.cts.entity.PackageItinerary;
import com.cts.entity.TravelPackage;
import com.cts.exception.PackageItineraryNotFound;
import com.cts.exception.PackageNotFoundException;
import com.cts.repository.PackageItineraryRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.PackageItineraryService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PackageItineraryServiceImpl implements PackageItineraryService {

    private final PackageItineraryRepository itineraryRepository;
    private final TravelPackageRepository travelPackageRepository;

    @Override
    public PackageItinerary save(PackageItineraryRequestDTO dto) {
        TravelPackage pkg = travelPackageRepository.findById(dto.getPackageId())
                .orElseThrow(() -> new PackageNotFoundException("Package not found with id: " + dto.getPackageId()));

        PackageItinerary itinerary = PackageItinerary.builder()
                .startDate(dto.getStart_date())
                .endDate(dto.getEnd_date())
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .createdAt(java.time.LocalDateTime.now())
                .travelPackage(pkg)
                .build();

        return itineraryRepository.save(itinerary);
    }

    @Override
    public List<PackageItinerary> getAll() {
        return itineraryRepository.findAll();
    }

    @Override
    public PackageItineraryResponceDTO getItineraryById(Long id) {
        PackageItinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new PackageItineraryNotFound("Itinerary not found with id: " + id));

        TravelPackage pkg = itinerary.getTravelPackage();

        return new PackageItineraryResponceDTO(
                pkg.getPackageId(),
                pkg.getPackageName(),
                pkg.getDescription(),
                pkg.getDurationDays(),
                pkg.getPrice(),
                pkg.getStatus(),
                pkg.getDestination(),
                itinerary.getPackageItineraryId(),
                itinerary.getStartDate(),
                itinerary.getEndDate(),
                itinerary.getStatus(),
                itinerary.getNotes(),
                itinerary.getCreatedAt(),
                itinerary.getDetailedDescription(),
                itinerary.getKeyHighlights(),
                itinerary.getGuideName(),
                itinerary.getSupportContact()
        );
    }

    @Override
    public void delete(Long id) {
        if (!itineraryRepository.existsById(id)) {
            throw new PackageItineraryNotFound("Itinerary not found with id: " + id);
        }
        itineraryRepository.deleteById(id);
    }

	
}
