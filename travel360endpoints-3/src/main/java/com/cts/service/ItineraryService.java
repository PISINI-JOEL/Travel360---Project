package com.cts.service;

import com.cts.dto.AddBookingDTO;
import com.cts.dto.CreateItineraryDTO;
import com.cts.dto.ItineraryResponseDTO;

import java.util.List;

public interface ItineraryService {

	ItineraryResponseDTO createItinerary(CreateItineraryDTO dto);

	ItineraryResponseDTO addBookingToItinerary(AddBookingDTO dto);

	ItineraryResponseDTO removeBookingFromItinerary(AddBookingDTO dto);

	List<ItineraryResponseDTO> getUserItineraries(Long userId);

	ItineraryResponseDTO getItineraryById(Long itineraryId, Long userId);

	ItineraryResponseDTO updateItinerary(Long itineraryId, CreateItineraryDTO dto);

	void deleteItinerary(Long itineraryId, Long userId);
}
