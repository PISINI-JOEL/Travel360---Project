package com.cts.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;

public interface HotelService {
	
	

	    Hotel addHotel(HotelDTO dto);

	    Hotel updateHotel(Long id, HotelDTO dto);

	    List<Hotel> findByLocation(String location, int page, int size);
	    
	    

	    List<Hotel> getFilteredHotels(String location, Integer ratings, Double minPrice, Double maxPrice, int page, int size);
	


}
