package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;
import com.cts.enums.HotelStatus;
import com.cts.repository.HotelRepository;
import com.cts.service.HotelService;

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
    public List<Hotel> findByLocation(String location) {

        return hotelrepo.findByCity(location);
    }

    
    


    @Override
    public List<Hotel> getFilteredHotels(
            String location,
            Integer ratings,
            Double minPrice,
            Double maxPrice) {

        List<Hotel> hotels = hotelrepo.filterHotels(
                location,
                ratings,
                minPrice,
                maxPrice
        );

        return hotels.stream()
                .map(h -> Hotel.builder()
                        .hotelName(h.getHotelName())   
                        .hotelId(h.getHotelId())
                        .ratings(h.getRatings())
                        .price(h.getPrice())
                        .status(h.getStatus())
                        .contactNo(h.getContactNo())
                        .emailId(h.getEmailId())
                        .city(h.getCity())
                        .build())
                .toList();
    }
}