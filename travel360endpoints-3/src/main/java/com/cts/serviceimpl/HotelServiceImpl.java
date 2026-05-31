package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;
import com.cts.entity.Partner;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.exception.HotelNotFoundException;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.HotelRepository;
import com.cts.repository.PartnerRepository;
import com.cts.service.HotelService;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelrepo;
    private final PartnerRepository partnerRepo;


    @Override
    public Hotel addHotel(HotelDTO dto) {

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.HOTEL) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a HOTEL partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        Hotel hotel = Hotel.builder()
                .hotelName(dto.getHotelName())
                .city(dto.getCity())
                .price(dto.getPrice())
                .ratings(dto.getRatings())
                .contactNo(dto.getContactNo())
                .emailId(dto.getEmailId())
                .totalRooms(dto.getTotalRooms())
                .status(dto.getStatus())
                .partner(partner)
                .build();

        return hotelrepo.save(hotel);
    }

    @Override
    @Transactional
    public Hotel updateHotel(Long id, HotelDTO dto) {

        Hotel hotel = hotelrepo.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found"));

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.HOTEL) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a HOTEL partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        hotel.setHotelName(dto.getHotelName());
        hotel.setCity(dto.getCity());
        hotel.setPrice(dto.getPrice());
        hotel.setRatings(dto.getRatings());
        hotel.setContactNo(dto.getContactNo());
        hotel.setEmailId(dto.getEmailId());
        hotel.setTotalRooms(dto.getTotalRooms());
        hotel.setStatus(dto.getStatus());
        hotel.setPartner(partner);

        return hotelrepo.save(hotel);
    }

    @Override
    public List<Hotel> getFilteredHotels(
            String location,
            Integer ratings,
            Double minPrice,
            Double maxPrice,int page,int size) {

    	Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotelPage = hotelrepo.filterHotels(
                location,
                ratings,
                minPrice,
                maxPrice,pageable
        );

        return hotelPage.stream()
                .map(h -> Hotel.builder()
                        .hotelName(h.getHotelName())   
                        .hotelId(h.getHotelId())
                        .ratings(h.getRatings())
                        .price(h.getPrice())
                        .contactNo(h.getContactNo())
                        .emailId(h.getEmailId())
                        .city(h.getCity())
                        .build())
                .toList();
    }


	@Override
	public List<Hotel> findByLocation(String location, int page, int size) {
		// TODO Auto-generated method stub
		 Pageable pageable = PageRequest.of(page, size);
		 Page<Hotel> hotelPage= hotelrepo.findByCity(location,pageable);
		return hotelPage.getContent();
	}
}