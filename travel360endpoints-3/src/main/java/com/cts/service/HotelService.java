package com.cts.service;

import java.util.List;

import com.cts.dto.HotelDTO;
import com.cts.entity.Hotel;

public interface HotelService {
	
	

	    Hotel addHotel(HotelDTO dto);

	    List<Hotel> findByLocation(String location);
	    
	    

	    List<Hotel> getFilteredHotels(String location, Integer ratings, Double minPrice, Double maxPrice);
	


}
