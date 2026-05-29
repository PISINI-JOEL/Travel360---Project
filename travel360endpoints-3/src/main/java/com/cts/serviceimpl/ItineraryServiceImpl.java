package com.cts.serviceimpl;

import com.cts.dto.AddBookingDTO;
import com.cts.dto.BookingResponseDTO;
import com.cts.dto.CreateItineraryDTO;
import com.cts.dto.ItineraryResponseDTO;
import com.cts.entity.Booking;
import com.cts.entity.Itinerary;
import com.cts.entity.User;
import com.cts.exception.InvalidBookingException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.exception.UserNotFoundException;
import com.cts.repository.BookingRepository;
import com.cts.repository.ItineraryRepository;
import com.cts.repository.UserRepository;
import com.cts.service.ItineraryService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {

	private final ItineraryRepository itineraryRepo;
	private final BookingRepository bookingRepo;
	private final UserRepository userRepo;

	@Override
	@Transactional
	public ItineraryResponseDTO createItinerary(CreateItineraryDTO dto) {

		User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

		if (!dto.getEndDate().isAfter(dto.getStartDate())) {
			throw new InvalidBookingException("End date must be after start date");
		}

		Itinerary itinerary = Itinerary.builder().tripName(dto.getTripName()).description(dto.getDescription())
				.startDate(dto.getStartDate()).endDate(dto.getEndDate()).createdAt(LocalDateTime.now()).user(user)
				.bookings(new ArrayList<>()) // Initialize list to prevent NullPointerExceptions
				.build();

		itinerary = itineraryRepo.save(itinerary);

		return mapToDTO(itinerary);
	}

	@Override
	@Transactional
	public ItineraryResponseDTO addBookingToItinerary(AddBookingDTO dto) {

		Itinerary itinerary = itineraryRepo.findById(dto.getItineraryId())
				.orElseThrow(() -> new ResourceNotFoundException("Itinerary not found"));

		Booking booking = bookingRepo.findById(dto.getBookingId())
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		if (!booking.getUser().getUserId().equals(itinerary.getUser().getUserId())) {
			throw new InvalidBookingException("Booking does not belong to the itinerary owner");
		}

		if (booking.getItinerary() != null) {
			throw new InvalidBookingException("Booking already belongs to an itinerary");
		}

		// Synchronize the bidirectional relationship in-memory
		booking.setItinerary(itinerary);
		if (itinerary.getBookings() == null) {
			itinerary.setBookings(new ArrayList<>());
		}
		itinerary.getBookings().add(booking);

		bookingRepo.save(booking);

		return mapToDTO(itinerary);
	}

	@Override
	@Transactional
	public ItineraryResponseDTO removeBookingFromItinerary(AddBookingDTO dto) {

		Itinerary itinerary = itineraryRepo.findById(dto.getItineraryId())
				.orElseThrow(() -> new ResourceNotFoundException("Itinerary not found"));

		Booking booking = bookingRepo.findById(dto.getBookingId())
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		if (booking.getItinerary() == null
				|| !booking.getItinerary().getItineraryId().equals(itinerary.getItineraryId())) {
			throw new InvalidBookingException("Booking does not belong to this itinerary");
		}

		// Synchronize the bidirectional relationship in-memory during removal
		booking.setItinerary(null);
		if (itinerary.getBookings() != null) {
			itinerary.getBookings().remove(booking);
		}

		bookingRepo.save(booking);

		return mapToDTO(itinerary);
	}

	@Override
	public List<ItineraryResponseDTO> getUserItineraries(Long userId) {

		List<Itinerary> list = itineraryRepo.findByUserUserId(userId);

		return list.stream().map(this::mapToDTO).toList();
	}

	@Override
	public ItineraryResponseDTO getItineraryById(Long itineraryId, Long userId) {

		Itinerary itinerary = itineraryRepo.findById(itineraryId)
				.orElseThrow(() -> new ResourceNotFoundException("Itinerary not found"));

		if (!itinerary.getUser().getUserId().equals(userId)) {
			throw new InvalidBookingException("Unauthorized access");
		}

		return mapToDTO(itinerary);
	}

	@Override
	@Transactional
	public ItineraryResponseDTO updateItinerary(Long itineraryId, CreateItineraryDTO dto) {

		Itinerary itinerary = itineraryRepo.findById(itineraryId)
				.orElseThrow(() -> new ResourceNotFoundException("Itinerary not found"));

		if (!itinerary.getUser().getUserId().equals(dto.getUserId())) {
			throw new InvalidBookingException("Unauthorized access");
		}

		if (!dto.getEndDate().isAfter(dto.getStartDate())) {
			throw new InvalidBookingException("End date must be after start date");
		}

		itinerary.setTripName(dto.getTripName());
		itinerary.setDescription(dto.getDescription());
		itinerary.setStartDate(dto.getStartDate());
		itinerary.setEndDate(dto.getEndDate());

		itinerary = itineraryRepo.save(itinerary);

		return mapToDTO(itinerary);
	}

	@Override
	@Transactional
	public void deleteItinerary(Long itineraryId, Long userId) {

		Itinerary itinerary = itineraryRepo.findById(itineraryId)
				.orElseThrow(() -> new ResourceNotFoundException("Itinerary not found"));

		if (!itinerary.getUser().getUserId().equals(userId)) {
			throw new InvalidBookingException("Unauthorized access");
		}

		List<Booking> bookings = bookingRepo.findByItineraryItineraryId(itineraryId);
		bookings.forEach(booking -> booking.setItinerary(null));
		bookingRepo.saveAll(bookings);

		itineraryRepo.delete(itinerary);
	}

	private ItineraryResponseDTO mapToDTO(Itinerary itinerary) {

		// Optimized to read straight from the object list, avoiding extra DB queries
		List<Booking> bookings = itinerary.getBookings() != null ? itinerary.getBookings() : new ArrayList<>();

		// Cleaned up with simpler arrow lambda syntax for sorting
		List<BookingResponseDTO> bookingDTOs = bookings.stream().sorted((b1, b2) -> {
			if (b1.getBookingDate() == null)
				return 1;
			if (b2.getBookingDate() == null)
				return -1;
			return b1.getBookingDate().compareTo(b2.getBookingDate());
		}).map(this::mapBookingToDTO).toList();

		double totalTripAmount = bookings.stream().mapToDouble(Booking::getAmount).sum();

		return ItineraryResponseDTO.builder().itineraryId(itinerary.getItineraryId()).tripName(itinerary.getTripName())
				.description(itinerary.getDescription()).startDate(itinerary.getStartDate())
				.endDate(itinerary.getEndDate()).createdAt(itinerary.getCreatedAt())
				.userId(itinerary.getUser().getUserId()).email(itinerary.getUser().getEmail()).bookings(bookingDTOs)
				.totalTripAmount(totalTripAmount).build();
	}

	private BookingResponseDTO mapBookingToDTO(Booking booking) {

		return BookingResponseDTO.builder().bookingId(booking.getBookingId()).bookingType(booking.getBookingType())
				.amount(booking.getAmount()).status(booking.getStatus()).userId(booking.getUser().getUserId())
				.email(booking.getUser().getEmail()).units(booking.getUnits())
				.flightId(booking.getFlight() != null ? booking.getFlight().getFlightId() : null)
				.flightNumber(booking.getFlight() != null ? booking.getFlight().getFlightNumber() : null)
				.hotelId(booking.getHotel() != null ? booking.getHotel().getHotelId() : null)
				.hotelName(booking.getHotel() != null ? booking.getHotel().getHotelName() : null)
				.transportId(booking.getTransport() != null ? booking.getTransport().getTransportId() : null)
				.transportType(booking.getTransport() != null ? booking.getTransport().getTransportType() : null)
				.packageId(booking.getTravelPackage() != null ? booking.getTravelPackage().getPackageId() : null)
				.packageName(booking.getTravelPackage() != null ? booking.getTravelPackage().getPackageName() : null)
				.itineraryId(booking.getItinerary() != null ? booking.getItinerary().getItineraryId() : null).build();
	}
}