package com.cts.serviceimpl;

import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.entity.Flight;
import com.cts.entity.Hotel;
import com.cts.entity.Partner;
import com.cts.entity.Transport;
import com.cts.entity.TravelPackage;
import com.cts.enums.FlightStatus;
import com.cts.enums.HotelStatus;
import com.cts.enums.PackageStatus;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.enums.TransportStatus;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.FlightRepository;
import com.cts.repository.HotelRepository;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TransportRepository;
import com.cts.repository.TravelPackageRepository;
import com.cts.service.PartnerService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepo;
    private final FlightRepository flightRepo;
    private final HotelRepository hotelRepo;
    private final TransportRepository transportRepo;
    private final TravelPackageRepository packageRepo;

    // ✅ GET BY ID
    @Override
    public PartnerResponseDTO getPartnerById(Long id) {

        log.info("Fetching partner with ID: {}", id);

        Partner partner = partnerRepo.findById(id).orElse(null);

        if (partner == null) {
            log.error("Partner not found with id {}", id);
            throw new PartnerNotFoundException("Partner not found");
        }

        return mapToDTO(partner);
    }

    // ✅ UPDATE
    @Override
    @Transactional
    public PartnerResponseDTO updatePartner(Long id, PartnerDTO dto) {

        log.info("Updating partner with ID: {}", id);

        Partner partner = partnerRepo.findById(id).orElse(null);

        if (partner == null) {
            log.error("Partner not found with id {}", id);
            throw new PartnerNotFoundException("Partner not found");
        }

        partner.setName(dto.getName());
        partner.setType(dto.getType());
        partner.setStatus(dto.getStatus());

        partner = partnerRepo.save(partner);

        log.info("Partner updated successfully with ID: {}", partner.getPartnerId());

        if (isDisabled(partner.getStatus())) {
            log.debug("Partner {} is not ACTIVE, deactivating inventory", partner.getPartnerId());
            deactivateInventory(partner);
        }

        return mapToDTO(partner);
    }

    // ✅ DELETE
    @Override
    @Transactional
    public void deletePartner(Long id) {

        log.info("Deleting (deactivating) partner with ID: {}", id);

        Partner partner = partnerRepo.findById(id).orElse(null);

        if (partner == null) {
            log.error("Partner not found with id {}", id);
            throw new PartnerNotFoundException("Partner not found");
        }

        partner.setStatus(PartnerStatus.INACTIVE);
        partnerRepo.save(partner);

        log.info("Partner deactivated successfully with ID: {}", partner.getPartnerId());

        deactivateInventory(partner);
    }

    // A partner that is not ACTIVE can no longer sell inventory.
    private boolean isDisabled(PartnerStatus status) {
        return status != PartnerStatus.ACTIVE;
    }

    // Cascade: take all of this partner's inventory out of sale.
    private void deactivateInventory(Partner partner) {

        log.debug("Deactivating inventory for partner {} of type {}",
                partner.getPartnerId(), partner.getType());

        switch (partner.getType()) {

            case FLIGHT -> {
                List<Flight> flights = flightRepo.findByPartner(partner);
                flights.forEach(f -> f.setStatus(FlightStatus.CANCELLED));
                flightRepo.saveAll(flights);
            }
            case HOTEL -> {
                List<Hotel> hotels = hotelRepo.findByPartner(partner);
                hotels.forEach(h -> h.setStatus(HotelStatus.INACTIVE));
                hotelRepo.saveAll(hotels);
            }
            case BUS -> {
                List<Transport> transports = transportRepo.findByPartner(partner);
                transports.forEach(t -> t.setTransportStatus(TransportStatus.OUT_OF_SERVICE));
                transportRepo.saveAll(transports);
            }
            case PACKAGE -> {
                List<TravelPackage> packages = packageRepo.findByPartner(partner);
                packages.forEach(p -> p.setStatus(PackageStatus.INACTIVE));
                packageRepo.saveAll(packages);
            }
        }
    }

    // ✅ GET BY CATEGORY
    @Override
    public List<PartnerResponseDTO> getPartnerByCategory(PartnerType type) {

        log.info("Fetching partners by category: {}", type);

        List<Partner> list = partnerRepo.findByType(type);

        log.info("Found {} partners for category {}", list.size(), type);

        return list.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ✅ COMMON MAPPER
    private PartnerResponseDTO mapToDTO(Partner partner) {

        return PartnerResponseDTO.builder()
                .partnerId(partner.getPartnerId())
                .name(partner.getName())
                .type(partner.getType())
                .status(partner.getStatus())
                .build();
    }

	
    

    @Override
     public PartnerResponseDTO createPartner(PartnerDTO dto) {

    log.info("Creating new partner of type: {}", dto.getType());

    Partner partner = Partner.builder()
            .name(dto.getName())
            .type(dto.getType())
            .status(dto.getStatus())
            .build();

    partner = partnerRepo.save(partner);

    log.info("Partner created successfully with ID: {}", partner.getPartnerId());

    return mapToDTO(partner);
}

}
