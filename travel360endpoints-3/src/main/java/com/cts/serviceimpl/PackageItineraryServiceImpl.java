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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PackageItineraryServiceImpl implements PackageItineraryService {

    private final PackageItineraryRepository itineraryRepository;
    private final TravelPackageRepository travelPackageRepository;

    @Override
    public PackageItinerary save(PackageItineraryRequestDTO dto) {
        log.info("Saving package itinerary for packageId: {}", dto.getPackageId());

        TravelPackage pkg = travelPackageRepository.findById(dto.getPackageId())
                .orElseThrow(() -> {
                    log.error("Package not found with id {}", dto.getPackageId());
                    return new PackageNotFoundException("Package not found with id: " + dto.getPackageId());
                });

        PackageItinerary itinerary = PackageItinerary.builder()
                .startDate(dto.getStart_date())
                .endDate(dto.getEnd_date())
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .createdAt(java.time.LocalDateTime.now())
                .travelPackage(pkg)
                .build();

        PackageItinerary saved = itineraryRepository.save(itinerary);

        log.info("Package itinerary created successfully with ID: {}", saved.getPackageItineraryId());

        return saved;
    }

    @Override
    public List<PackageItinerary> getAll() {
        log.info("Fetching all package itineraries");

        List<PackageItinerary> itineraries = itineraryRepository.findAll();

        log.info("Total package itineraries fetched: {}", itineraries.size());

        return itineraries;
    }

    @Override
    public PackageItineraryResponceDTO getItineraryById(Long id) {
        log.info("Fetching package itinerary with ID: {}", id);

        PackageItinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Package itinerary not found with id {}", id);
                    return new PackageItineraryNotFound("Itinerary not found with id: " + id);
                });

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
        log.info("Deleting package itinerary with ID: {}", id);

        if (!itineraryRepository.existsById(id)) {
            log.error("Package itinerary not found with id {}", id);
            throw new PackageItineraryNotFound("Itinerary not found with id: " + id);
        }
        itineraryRepository.deleteById(id);

        log.info("Package itinerary deleted successfully with ID: {}", id);
    }

	
}
