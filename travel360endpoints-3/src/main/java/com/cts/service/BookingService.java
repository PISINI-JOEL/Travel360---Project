package com.cts.service;

import com.cts.dto.BookingCancelDTO;
import com.cts.dto.BookingCancelResponseDTO;
import com.cts.dto.BookingDTO;
import com.cts.dto.BookingFlightDTO;
import com.cts.dto.BookingFlightResponseDTO;
import com.cts.dto.BookingHotelDTO;
import com.cts.dto.BookingHotelResponseDTO;
import com.cts.dto.BookingPackageDTO;
import com.cts.dto.BookingPackageResponseDTO;
import com.cts.dto.BookingResponseDTO;
import com.cts.dto.BookingTransportDTO;
import com.cts.dto.BookingTransportResponseDTO;
import com.cts.dto.PassengerCancelResponseDTO;
import com.cts.entity.Booking;

import java.util.List;

public interface BookingService {

	BookingFlightResponseDTO createFlightBooking(BookingFlightDTO dto);

	BookingHotelResponseDTO createHotelBooking(BookingHotelDTO dto);

	BookingPackageResponseDTO createPackageBooking(BookingPackageDTO dto);
	
	BookingTransportResponseDTO createTransportBooking(BookingTransportDTO dto);

	List<BookingResponseDTO> getBookingsByUser(Long userId);

	List<BookingResponseDTO> getAllBookings();

	BookingCancelResponseDTO deleteBooking(BookingCancelDTO dto);

	PassengerCancelResponseDTO cancelPassenger(Long bookingId, Long passengerId, Long userId);
}