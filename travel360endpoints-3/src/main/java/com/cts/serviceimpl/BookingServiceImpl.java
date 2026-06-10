package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.repository.BookingRepository;
import com.cts.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

	private final FlightBookingDelegate flightDelegate;
	private final HotelBookingDelegate hotelDelegate;
	private final TransportBookingDelegate transportDelegate;
	private final PackageBookingDelegate packageDelegate;
	private final BookingCancelDelegate cancelDelegate;
	private final PassengerDelegate passengerDelegate;
	private final BookingRepository bookingRepo;
	private final BookingHelper helper;
	private final AuthenticatedUserProvider authUser;

	@Override
	public BookingFlightResponseDTO createFlightBooking(BookingFlightDTO dto) {
		return flightDelegate.createFlightBooking(dto);
	}

	@Override
	public BookingHotelResponseDTO createHotelBooking(BookingHotelDTO dto) {
		return hotelDelegate.createHotelBooking(dto);
	}

	@Override
	public BookingTransportResponseDTO createTransportBooking(BookingTransportDTO dto) {
		return transportDelegate.createTransportBooking(dto);
	}

	@Override
	public BookingPackageResponseDTO createPackageBooking(BookingPackageDTO dto) {
		return packageDelegate.createPackageBooking(dto);
	}

	@Override
	public BookingCancelResponseDTO deleteBooking(BookingCancelDTO dto) {
		return cancelDelegate.deleteBooking(dto);
	}

	@Override
	public PassengerCancelResponseDTO cancelPassenger(Long bookingId, Long passengerId, Long userId) {
		return passengerDelegate.cancelPassenger(bookingId, passengerId, userId);
	}

	@Override
	public List<BookingResponseDTO> getBookingsByUser(Long userId) {
		authUser.assertCanActAs(userId);
		log.info("Fetching bookings for userId: {}", userId);
		return bookingRepo.findByUserUserId(userId).stream().map(helper::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public List<BookingResponseDTO> getAllBookings() {
		log.info("Fetching all bookings");
		return bookingRepo.findAll().stream().map(helper::mapToDTO).collect(Collectors.toList());
	}
}