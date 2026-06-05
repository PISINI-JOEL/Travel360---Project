package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;
import com.cts.entity.Partner;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.exception.HotelNotFoundException;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.HotelRepository;
import com.cts.repository.PartnerRepository;
import com.cts.service.AuditLogService;
import com.cts.service.HotelService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelrepo;
    private final PartnerRepository partnerRepo;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @Override
    public Hotel addHotel(HotelDTO dto) {

        log.info("Adding new hotel with partnerId: {}", dto.getPartnerId());

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.HOTEL) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a HOTEL partner");
        }

        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        Hotel hotel = Hotel.builder()
                .hotelName(dto.getHotelName())
                .city(dto.getCity())
                .address(dto.getAddress())
                .price(dto.getPrice())
                .ratings(dto.getRatings())
                .contactNo(dto.getContactNo())
                .emailId(dto.getEmailId())
                .totalRooms(dto.getTotalRooms())
                .status(dto.getStatus())
                .partner(partner)
                .build();

        Hotel savedHotel = hotelrepo.save(hotel);
        auditLogService.logAction(AuditActions.CREATE_HOTEL, AuditEntity.HOTEL, savedHotel.getHotelId(), authUser.currentOrNull(), LogType.INFO);

        log.info("Hotel created successfully with ID: {}", savedHotel.getHotelId());

        return savedHotel;
    }

    @Override
    @Transactional
    public Hotel updateHotel(Long id, HotelDTO dto) {

        log.info("Updating hotel with ID: {}", id);

        Hotel hotel = hotelrepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel not found with id {}", id);
                    return new HotelNotFoundException("Hotel not found");
                });

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.HOTEL) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a HOTEL partner");
        }

        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        log.debug("Updating fields for hotel ID: {}", id);

        hotel.setHotelName(dto.getHotelName());
        hotel.setCity(dto.getCity());
        hotel.setAddress(dto.getAddress());
        hotel.setPrice(dto.getPrice());
        hotel.setRatings(dto.getRatings());
        hotel.setContactNo(dto.getContactNo());
        hotel.setEmailId(dto.getEmailId());
        hotel.setTotalRooms(dto.getTotalRooms());
        hotel.setStatus(dto.getStatus());
        hotel.setPartner(partner);

        Hotel updatedHotel = hotelrepo.save(hotel);
        auditLogService.logAction(AuditActions.UPDATE_HOTEL, AuditEntity.HOTEL, updatedHotel.getHotelId(), authUser.currentOrNull(), LogType.INFO);

        log.info("Hotel updated successfully with ID: {}", id);

        return updatedHotel;
    }

    @Override
    public List<Hotel> getFilteredHotels(
            String location,
            Integer ratings,
            Double minPrice,
            Double maxPrice,
            int page,
            int size) {

        log.info("Filtering hotels with location={}, ratings={}, minPrice={}, maxPrice={}, page={}, size={}",
                location, ratings, minPrice, maxPrice, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Hotel> hotelPage = hotelrepo.filterHotels(
                location,
                ratings,
                minPrice,
                maxPrice,
                pageable
        );

        log.info("Filter query returned {} hotels", hotelPage.getTotalElements());

        return hotelPage.stream()
                .map(h -> Hotel.builder()
                        .hotelName(h.getHotelName())
                        .status(h.getStatus())
                        .hotelId(h.getHotelId())
                        .partner(h.getPartner())
                        .ratings(h.getRatings())
                        .price(h.getPrice())
                        .contactNo(h.getContactNo())
                        .emailId(h.getEmailId())
                        .totalRooms(h.getTotalRooms())
                        .city(h.getCity())
                        .build())
                .toList();
    }

    @Override
    public List<Hotel> findByLocation(String location, int page, int size) {

        log.info("Fetching hotels by location '{}' (page={}, size={})",
                location, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Hotel> hotelPage = hotelrepo.findByCity(location, pageable);

        log.info("Found {} hotels in location '{}'",
                hotelPage.getContent().size(), location);

        return hotelPage.getContent();
    }
}
