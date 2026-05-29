package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;
import com.cts.enums.HotelStatus;
import com.cts.repository.HotelRepository;
import com.cts.service.HotelService;

import jakarta.validation.ReportAsSingleViolation;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelrepo;   

   
    @Override
    public Hotel addHotel(HotelDTO dto) {

        Hotel hotel = Hotel.builder()
                .hotelName(dto.getHotelName())
                .city(dto.getCity())
                .price(dto.getPrice())
                .ratings(dto.getRatings())
                .contactNo(dto.getContactNo())
                .emailId(dto.getEmailId())
                .status(dto.getStatus())
                .totalRooms(dto.getTotalRooms())
                .status(dto.getStatus())
                .build();

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