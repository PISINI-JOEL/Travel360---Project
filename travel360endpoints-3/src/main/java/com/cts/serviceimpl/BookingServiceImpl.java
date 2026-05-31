package com.cts.serviceimpl;

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
import com.cts.dto.PassengerDTO;
import com.cts.dto.PassengerResponseDTO;
import com.cts.entity.*;
import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.FlightStatus;
import com.cts.enums.HotelStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.PackageStatus;
import com.cts.enums.PassengerStatus;
import com.cts.enums.PaymentStatus;
import com.cts.enums.TransportStatus;
import com.cts.exception.FlightNotFoundException;
import com.cts.exception.HotelNotFoundException;
import com.cts.exception.InsufficientAvailabilityException;
import com.cts.exception.InvalidBookingException;
import com.cts.exception.PackageNotFoundException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.exception.TransportNotFoundException;
import com.cts.exception.UserNotFoundException;
import com.cts.repository.*;
import com.cts.service.BookingService;
import com.cts.service.NotificationService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepo;
	private final UserRepository userRepo;
	private final FlightRepository flightRepo;
	private final HotelRepository hotelrepo;
	private final InvoiceRepository invoiceRepo;
	private final TravelPackageRepository packageRepo;
	private final NotificationService notificationService;
	private final TransportRepository transportRepo;
	private final PaymentRepository paymentRepo;
	private final PassengerRepository passengerRepo;

	@Override
	@Transactional
	public BookingFlightResponseDTO createFlightBooking(BookingFlightDTO dto) {

		User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

		Flight flight = flightRepo.findById(dto.getFlightId())
				.orElseThrow(() -> new FlightNotFoundException("Flight not found"));

		if (flight.getStatus() != FlightStatus.SCHEDULED) {
			throw new InvalidBookingException("Flight is not available for booking");
		}

		int totalSeats = flight.getTotalSeats();
		int bookedSeats = bookingRepo.getBookedSeats(flight.getFlightId(), dto.getFlightDate());
		int availableSeats = totalSeats - bookedSeats;
		if (availableSeats < dto.getUnits()) {
			throw new InsufficientAvailabilityException("Not enough seats available");
		}
		if (dto.getFlightDate() == null || !dto.getFlightDate().equals(flight.getFlightDate())) {
			throw new InvalidBookingException("Booking info not valid");
		}

		LocalDate today = LocalDate.now();
		LocalDate flightDate = flight.getFlightDate();

		if (!flightDate.isAfter(today.plusDays(1))) {
			throw new InvalidBookingException("Booking is not allowed 1 day before or on the same day of the flight");
		}

		validatePassengerCount(dto.getPassengers(), dto.getUnits());

		Booking booking = Booking.builder().user(user).flight(flight).bookingType(BookingType.FLIGHT)
				.bookingName(dto.getBookingName()).gender(dto.getGender()).amount(flight.getPrice() * dto.getUnits())
				.units(dto.getUnits()).days(1).createdAt(LocalDateTime.now()).status(BookingStatus.PENDING)
				.bookingDate(flight.getFlightDate()).build();

		booking.setPassengers(buildPassengers(dto.getPassengers(), booking));
		bookingRepo.save(booking);

		Invoice invoice = Invoice.builder().booking(booking).invoiceDate(LocalDateTime.now())
				.amount(booking.getAmount()).status(PaymentStatus.PENDING).build();

		invoiceRepo.save(invoice);
		notificationService.sendNotification(user, "Flight booked successfully. Booking ID: " + booking.getBookingId(),
				NotificationCategory.BOOKING);

		return BookingFlightResponseDTO.builder().bookingId(booking.getBookingId())
				.bookingType(booking.getBookingType()).amount(booking.getAmount()).status(booking.getStatus())
				.userId(user.getUserId()).email(user.getEmail()).units(dto.getUnits()).createdAt(booking.getCreatedAt())
				.bookingDate(booking.getBookingDate()).arrivalTime(flight.getArrivalTime())
				.departureTime(flight.getDepartureTime()).flightDate(flight.getFlightDate())
				.bookingName(booking.getBookingName()).gender(booking.getGender()).flightId(flight.getFlightId())
				.flightNumber(flight.getFlightNumber()).source(flight.getSource()).destination(flight.getDestination())
				.passengers(mapPassengers(booking.getPassengers())).build();
	}

	@Override
	@Transactional
	public BookingHotelResponseDTO createHotelBooking(BookingHotelDTO dto) {

		User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

		Hotel hotel = hotelrepo.findById(dto.getHotelId())
				.orElseThrow(() -> new HotelNotFoundException("Hotel not found"));

		int totalRooms = hotel.getTotalRooms();
		int bookedRooms = bookingRepo.getBookedRooms(hotel.getHotelId());
		int availableRooms = totalRooms - bookedRooms;

		if (availableRooms < dto.getUnits()) {
			throw new InsufficientAvailabilityException("Not enough rooms available");
		}
		long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
		if (days <= 0) {
			throw new InvalidBookingException("Check-out date must be after check-in date");
		}

		if (hotel.getStatus() != HotelStatus.AVAILABLE) {
			throw new InvalidBookingException("Hotel is not available for booking");
		}

		Booking booking = Booking.builder().user(user).hotel(hotel).bookingType(BookingType.HOTEL)
				.bookingName(dto.getBookingName()).gender(dto.getGender()).units(dto.getUnits()).days((int) days)
				.checkInDate(dto.getCheckInDate()).checkOutDate(dto.getCheckOutDate())
				.amount(hotel.getPrice() * dto.getUnits() * days).status(BookingStatus.PENDING)
				.bookingDate(LocalDate.now()).build();

		bookingRepo.save(booking);
		Invoice invoice = Invoice.builder().booking(booking).invoiceDate(LocalDateTime.now())
				.amount(booking.getAmount()).status(PaymentStatus.PENDING).build();

		invoiceRepo.save(invoice);
		notificationService.sendNotification(user, "Hotel booked successfully. Booking ID: " + booking.getBookingId(),
				NotificationCategory.BOOKING);

		return BookingHotelResponseDTO.builder().bookingId(booking.getBookingId()).bookingType(booking.getBookingType())
				.amount(booking.getAmount()).status(booking.getStatus()).userId(user.getUserId()).email(user.getEmail())
				.units(dto.getUnits()).days(booking.getDays()).checkInDate(booking.getCheckInDate())
				.checkOutDate(booking.getCheckOutDate()).bookingName(booking.getBookingName())
				.gender(booking.getGender()).hotelId(hotel.getHotelId()).hotelName(hotel.getHotelName())
				.city(hotel.getCity()).build();
	}

	@Override
	@Transactional
	public BookingPackageResponseDTO createPackageBooking(BookingPackageDTO dto) {

		User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

		TravelPackage tpackage = packageRepo.findById(dto.getPackageId())
				.orElseThrow(() -> new PackageNotFoundException("Package not found"));

		if (tpackage.getStatus() != PackageStatus.AVAILABLE) {
			throw new InvalidBookingException("Package is not available for booking");
		}

		int totalSlots = tpackage.getTotalSlots();
		int bookedSlots = bookingRepo.getBookedSlots(tpackage.getPackageId());
		int availableSlots = totalSlots - bookedSlots;
		if (availableSlots < dto.getUnits()) {
			throw new InsufficientAvailabilityException("Not enough package slots available");
		}

		Booking booking = Booking.builder().user(user).travelPackage(tpackage).bookingType(BookingType.PACKAGE)
				.bookingName(dto.getBookingName()).gender(dto.getGender()).units(dto.getUnits())
				.amount(tpackage.getPrice() * dto.getUnits()).status(BookingStatus.PENDING)
				.bookingDate(tpackage.getStartDate() != null ? tpackage.getStartDate() : LocalDate.now()).build();

		booking = bookingRepo.save(booking);

		Invoice invoice = Invoice.builder().booking(booking).invoiceDate(LocalDateTime.now())
				.amount(booking.getAmount()).status(PaymentStatus.PENDING).build();

		invoiceRepo.save(invoice);
		notificationService.sendNotification(user, "Package booked successfully. Booking ID: " + booking.getBookingId(),
				NotificationCategory.BOOKING);

		return BookingPackageResponseDTO.builder().bookingId(booking.getBookingId())
				.bookingType(booking.getBookingType()).amount(booking.getAmount()).status(booking.getStatus())
				.bookingDate(LocalDate.now()).userId(user.getUserId()).email(user.getEmail()).units(dto.getUnits())
				.bookingName(booking.getBookingName()).gender(booking.getGender()).packageId(tpackage.getPackageId())
				.packageName(tpackage.getPackageName()).source(tpackage.getSource())
				.destination(tpackage.getDestination()).durationDays(tpackage.getDurationDays())
				.startDate(tpackage.getStartDate()).endDate(tpackage.getEndDate()).category(tpackage.getCategory())
				.packageStatus(tpackage.getStatus()).build();
	}

	@Override
	@Transactional
	public BookingTransportResponseDTO createTransportBooking(BookingTransportDTO dto) {

		User user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));

		Transport transport = transportRepo.findById(dto.getTransportId())
				.orElseThrow(() -> new TransportNotFoundException("Transport not found"));

		if (transport.getTransportStatus() != TransportStatus.AVAILABLE) {
			throw new InvalidBookingException("Transport is not available for booking");
		}

		int totalSeats = transport.getTransportTotalSeats();
		int bookedSeats = bookingRepo.getBookedTransportSeats(transport.getTransportId());
		int availableSeats = totalSeats - bookedSeats;
		if (availableSeats < dto.getUnits()) {
			throw new InsufficientAvailabilityException("Not enough seats available");
		}

		validatePassengerCount(dto.getPassengers(), dto.getUnits());

		Booking booking = Booking.builder().user(user).transport(transport).bookingType(BookingType.TRANSPORT)
				.bookingName(dto.getBookingName()).gender(dto.getGender()).units(dto.getUnits())
				.amount(transport.getPrice() * dto.getUnits()).status(BookingStatus.PENDING)
				.bookingDate(transport.getDepartureTime() != null ? transport.getDepartureTime().toLocalDate()
						: LocalDate.now())
				.build();

		booking.setPassengers(buildPassengers(dto.getPassengers(), booking));
		booking = bookingRepo.save(booking);

		Invoice invoice = Invoice.builder().booking(booking).invoiceDate(LocalDateTime.now())
				.amount(booking.getAmount()).status(PaymentStatus.PENDING).build();

		invoiceRepo.save(invoice);

		notificationService.sendNotification(user,
				"Transport booked from " + transport.getSource() + " to " + transport.getDestination(),
				NotificationCategory.BOOKING);

		return BookingTransportResponseDTO.builder().bookingId(booking.getBookingId())
				.bookingType(booking.getBookingType()).amount(booking.getAmount()).status(booking.getStatus())
				.bookingDate(booking.getBookingDate()).userId(user.getUserId()).email(user.getEmail())
				.units(dto.getUnits()).bookingName(booking.getBookingName()).gender(booking.getGender())
				.transportId(transport.getTransportId()).source(transport.getSource())
				.destination(transport.getDestination()).transportType(transport.getTransportType())
				.departureTime(transport.getDepartureTime()).arrivalTime(transport.getArrivalTime())
				.passengers(mapPassengers(booking.getPassengers())).build();
	}

	@Override
	public List<BookingResponseDTO> getBookingsByUser(Long userId) {
		List<Booking> list = bookingRepo.findByUserUserId(userId);
		return list.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	@Override
	public List<BookingResponseDTO> getAllBookings() {
		List<Booking> list = bookingRepo.findAll();
		return list.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	private BookingResponseDTO mapToDTO(Booking booking) {
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
				.itineraryId(booking.getItinerary() != null ? booking.getItinerary().getItineraryId() : null)
				.passengers(booking.getPassengers() != null && !booking.getPassengers().isEmpty() 
				? mapPassengers(booking.getPassengers()) 
				: null)
				.build();
	}

	@Override
	@Transactional
	public BookingCancelResponseDTO deleteBooking(BookingCancelDTO dto) {

		Booking booking = bookingRepo.findById(dto.getBookingId())
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		if (!booking.getUser().getUserId().equals(dto.getUserId())) {
			throw new InvalidBookingException("Booking does not belong to the given user");
		}

		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new InvalidBookingException("Booking is already cancelled");
		}

		LocalDateTime now = LocalDateTime.now();
		double refundAmount = 0.0;
		String refundStatus = "NONE";

		if (booking.getStatus() == BookingStatus.PENDING) {
			booking.setStatus(BookingStatus.CANCELLED);
			bookingRepo.save(booking);

			// No payment was made: void the still-unpaid invoice(s) so they are not
			// left dangling as an outstanding (PENDING) bill on a cancelled booking.
			invoiceRepo.findByBookingBookingId(booking.getBookingId()).stream()
					.filter(inv -> inv.getStatus() == PaymentStatus.PENDING)
					.forEach(inv -> {
						inv.setStatus(PaymentStatus.CANCELLED);
						invoiceRepo.save(inv);
					});

			notificationService.sendNotification(booking.getUser(),
					"Booking cancelled (no payment made). Booking ID: " + booking.getBookingId(),
					NotificationCategory.BOOKING);

			return BookingCancelResponseDTO.builder().bookingId(booking.getBookingId())
					.userId(booking.getUser().getUserId()).status(booking.getStatus())
					.originalAmount(booking.getAmount()).refundAmount(0.0).deductionAmount(booking.getAmount())
					.bookingDate(booking.getBookingDate()).cancelledAt(now).refundStatus("NONE")
					.message("Booking cancelled successfully (no payment made)").build();
		}

		if (booking.getStatus() == BookingStatus.CONFIRMED) {
			refundAmount = calculateRefundAmount(booking.getAmount(), booking.getBookingDate());

			if (refundAmount == booking.getAmount()) {
				refundStatus = "FULL";
			} else if (refundAmount > 0) {
				refundStatus = "PARTIAL";
			} else {
				refundStatus = "NONE";
			}

			booking.setStatus(BookingStatus.CANCELLED);
			bookingRepo.save(booking);

			Invoice refundInvoice = Invoice.builder().booking(booking).invoiceDate(now).amount(refundAmount)
					.status(PaymentStatus.REFUNDED).build();

			invoiceRepo.save(refundInvoice);

			Payment refundPayment = Payment.builder().invoice(refundInvoice).amount(refundAmount)
					.status(PaymentStatus.REFUNDED).paymentDate(now).paymentMethod("UPI").build();

			paymentRepo.save(refundPayment);

			notificationService.sendNotification(booking.getUser(),
					"Booking cancelled. Refund amount: " + refundAmount + " | Booking ID: " + booking.getBookingId(),
					NotificationCategory.BOOKING);

			return BookingCancelResponseDTO.builder().bookingId(booking.getBookingId())
					.userId(booking.getUser().getUserId()).status(booking.getStatus())
					.originalAmount(booking.getAmount()).refundAmount(refundAmount)
					.deductionAmount(booking.getAmount() - refundAmount).bookingDate(booking.getBookingDate())
					.cancelledAt(now).refundStatus(refundStatus).message("Booking cancelled successfully").build();
		}

		throw new InvalidBookingException("Invalid booking state");
	}

	private void createInvoice(Booking booking) {
		Invoice invoice = Invoice.builder().booking(booking).invoiceDate(LocalDateTime.now())
				.amount(booking.getAmount()).status(PaymentStatus.PENDING).build();
		invoiceRepo.save(invoice);
	}

	@Override
	@Transactional
	public PassengerCancelResponseDTO cancelPassenger(Long bookingId, Long passengerId, Long userId) {

		Booking booking = bookingRepo.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		if (!booking.getUser().getUserId().equals(userId)) {
			throw new InvalidBookingException("Booking does not belong to the given user");
		}

		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new InvalidBookingException("Booking is already cancelled");
		}

		if (booking.getBookingType() != BookingType.FLIGHT && booking.getBookingType() != BookingType.TRANSPORT) {
			throw new InvalidBookingException(
					"Passenger cancellation is only allowed for flight and transport bookings");
		}

		Passenger passenger = passengerRepo.findById(passengerId)
				.orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

		if (passenger.getBooking() == null || !passenger.getBooking().getBookingId().equals(bookingId)) {
			throw new InvalidBookingException("Passenger does not belong to the given booking");
		}

		if (passenger.getStatus() == PassengerStatus.CANCELLED) {
			throw new InvalidBookingException("Passenger is already cancelled");
		}

		LocalDateTime now = LocalDateTime.now();
		long activePassengers = passengerRepo.countByBookingBookingIdAndStatus(bookingId, PassengerStatus.ACTIVE);

		if (activePassengers <= 1) {
			BookingCancelDTO cancelDto = new BookingCancelDTO();
			cancelDto.setBookingId(bookingId);
			cancelDto.setUserId(userId);
			BookingCancelResponseDTO full = deleteBooking(cancelDto);

			passenger.setStatus(PassengerStatus.CANCELLED);
			passengerRepo.save(passenger);

			return PassengerCancelResponseDTO.builder().bookingId(bookingId).passengerId(passengerId)
					.passengerName(passenger.getPassengerName()).bookingStatus(BookingStatus.CANCELLED)
					.remainingUnits(0).refundAmount(full.getRefundAmount()).deductionAmount(full.getDeductionAmount())
					.refundStatus(full.getRefundStatus()).cancelledAt(now)
					.message("Last passenger removed; entire booking cancelled").build();
		}

		double perSeat = booking.getAmount() / booking.getUnits();
		double refundAmount = 0.0;
		String refundStatus = "NONE";

		if (booking.getStatus() == BookingStatus.CONFIRMED) {
			refundAmount = calculateRefundAmount(perSeat, booking.getBookingDate());

			if (refundAmount == perSeat) {
				refundStatus = "FULL";
			} else if (refundAmount > 0) {
				refundStatus = "PARTIAL";
			} else {
				refundStatus = "NONE";
			}
		}

		booking.setUnits(booking.getUnits() - 1);
		booking.setAmount(booking.getAmount() - perSeat);
		bookingRepo.save(booking);

		// Keep the original (still unpaid) invoice in sync with the reduced booking
		// amount. A paid SUCCESS invoice is left intact as an audit record of money
		// already collected; that giveback is captured by the REFUNDED invoice below.
		invoiceRepo.findByBookingBookingId(bookingId).stream()
				.filter(inv -> inv.getStatus() == PaymentStatus.PENDING)
				.findFirst()
				.ifPresent(inv -> {
					inv.setAmount(booking.getAmount());
					invoiceRepo.save(inv);
				});

		passenger.setStatus(PassengerStatus.CANCELLED);
		passengerRepo.save(passenger);

		if (refundAmount > 0) {
			Invoice refundInvoice = Invoice.builder().booking(booking).invoiceDate(now).amount(refundAmount)
					.status(PaymentStatus.REFUNDED).build();
			invoiceRepo.save(refundInvoice);

			Payment refundPayment = Payment.builder().invoice(refundInvoice).amount(refundAmount)
					.status(PaymentStatus.REFUNDED).paymentDate(now).paymentMethod("UPI").build();
			paymentRepo.save(refundPayment);
		}

		notificationService.sendNotification(booking.getUser(), "Passenger " + passenger.getPassengerName()
				+ " removed from booking " + bookingId + ". Refund: " + refundAmount, NotificationCategory.BOOKING);

		return PassengerCancelResponseDTO.builder().bookingId(bookingId).passengerId(passengerId)
				.passengerName(passenger.getPassengerName()).bookingStatus(booking.getStatus())
				.remainingUnits(booking.getUnits()).refundAmount(refundAmount).deductionAmount(perSeat - refundAmount)
				.refundStatus(refundStatus).cancelledAt(now).message("Passenger removed from booking").build();
	}

	private void validatePassengerCount(List<PassengerDTO> passengers, int units) {
		int count = passengers == null ? 0 : passengers.size();
		if (count != units) {
			throw new InvalidBookingException(
					"Passenger count (" + count + ") must match the number of units (" + units + ")");
		}
	}

	private List<Passenger> buildPassengers(List<PassengerDTO> passengers, Booking booking) {
		return passengers.stream()
				.map(p -> Passenger.builder().passengerName(p.getPassengerName()).dateOfBirth(p.getDateOfBirth())
						.gender(p.getGender()).contactNo(p.getContactNo()).emailAddress(p.getEmailAddress())
						.nationality(p.getNationality()).identificationNumber(p.getIdentificationNumber())
						.status(PassengerStatus.ACTIVE).booking(booking).build())
				.collect(Collectors.toList());
	}

	private List<PassengerResponseDTO> mapPassengers(List<Passenger> passengers) {
		return passengers.stream().map(this::toPassengerResponse).collect(Collectors.toList());
	}

	private PassengerResponseDTO toPassengerResponse(Passenger p) {
		return PassengerResponseDTO.builder().passengerId(p.getPassengerId()).passengerName(p.getPassengerName())
				.dateOfBirth(p.getDateOfBirth()).gender(p.getGender()).contactNo(p.getContactNo())
				.emailAddress(p.getEmailAddress()).nationality(p.getNationality())
				.identificationNumber(p.getIdentificationNumber()).status(p.getStatus()).build();
	}

	private double calculateRefundAmount(double amount, LocalDate bookingDate) {
		if (bookingDate == null) {
			throw new InvalidBookingException("Invalid booking date");
		}

		long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), bookingDate);

		if (daysBetween <= 0) {
			return 0.0;
		}

		if (daysBetween == 1) {
			throw new InvalidBookingException("Cancellation not allowed less than 1 day before booking date");
		}

		if (daysBetween > 7) {
			return amount;
		} else if (daysBetween >= 4) {
			return amount * 0.80;
		} else {
			return amount * 0.60;
		}
	}
}