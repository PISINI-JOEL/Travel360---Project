package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.*;
import com.cts.exception.*;
import com.cts.repository.*;
import com.cts.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingHelper {

    private final UserRepository userRepo;
    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;
    private final NotificationService notificationService;

    public User fetchUser(Long userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public void createPendingInvoice(Booking booking) {
        invoiceRepo.save(Invoice.builder().booking(booking)
            .invoiceDate(LocalDateTime.now()).amount(booking.getAmount())
            .status(PaymentStatus.PENDING).build());
    }

    public void createRefundInvoiceAndPayment(Booking booking, double refundAmount) {
        LocalDateTime now = LocalDateTime.now();
        Invoice refundInvoice = invoiceRepo.save(Invoice.builder().booking(booking)
            .invoiceDate(now).amount(refundAmount).status(PaymentStatus.REFUNDED).build());
        paymentRepo.save(Payment.builder().invoice(refundInvoice).amount(refundAmount)
            .status(PaymentStatus.REFUNDED).paymentDate(now).paymentMethod("UPI").build());
    }

    public void adjustPendingInvoice(Long bookingId, double newAmount) {
        invoiceRepo.findByBookingBookingId(bookingId).stream()
            .filter(inv -> inv.getStatus() == PaymentStatus.PENDING).findFirst()
            .ifPresent(inv -> { inv.setAmount(newAmount); invoiceRepo.save(inv); });
    }

    public double calculateRefundAmount(double amount, LocalDate bookingDate) {
        if (bookingDate == null) throw new InvalidBookingException("Invalid booking date");
        long days = ChronoUnit.DAYS.between(LocalDate.now(), bookingDate);
        if (days <= 0) return 0.0;
        if (days == 1) throw new InvalidBookingException("Cancellation not allowed less than 1 day before booking date");
        if (days > 7) return amount;
        if (days >= 4) return amount * 0.80;
        return amount * 0.60;
    }

    public String resolveRefundStatus(double refundAmount, double totalAmount) {
        if (refundAmount == totalAmount) return "FULL";
        if (refundAmount > 0) return "PARTIAL";
        return "NONE";
    }

    public void notify(User user, String message) {
        notificationService.sendNotification(user, message, NotificationCategory.BOOKING);
    }

    public void validatePassengerCount(List<PassengerDTO> passengers, int units) {
        int count = passengers == null ? 0 : passengers.size();
        if (count != units)
            throw new InvalidBookingException("Passenger count (" + count + ") must match units (" + units + ")");
    }

    public List<Passenger> buildPassengers(List<PassengerDTO> passengers, Booking booking) {
        return passengers.stream()
            .map(p -> Passenger.builder().passengerName(p.getPassengerName()).dateOfBirth(p.getDateOfBirth())
                .gender(p.getGender()).contactNo(p.getContactNo()).emailAddress(p.getEmailAddress())
                .nationality(p.getNationality()).identificationNumber(p.getIdentificationNumber())
                .status(PassengerStatus.ACTIVE).booking(booking).build())
            .collect(Collectors.toList());
    }

    public List<PassengerResponseDTO> mapPassengers(List<Passenger> passengers) {
        return passengers.stream()
            .map(p -> PassengerResponseDTO.builder().passengerId(p.getPassengerId())
                .passengerName(p.getPassengerName()).dateOfBirth(p.getDateOfBirth())
                .gender(p.getGender()).contactNo(p.getContactNo()).emailAddress(p.getEmailAddress())
                .nationality(p.getNationality()).identificationNumber(p.getIdentificationNumber())
                .status(p.getStatus()).build())
            .collect(Collectors.toList());
    }

    // ---- Booking Builders ----

    public Booking buildFlightBooking(User user, Flight flight, BookingFlightDTO dto) {
        Booking booking = Booking.builder().user(user).flight(flight).bookingType(BookingType.FLIGHT)
            .bookingName(dto.getBookingName()).gender(dto.getGender())
            .amount(flight.getPrice() * dto.getUnits()).units(dto.getUnits()).days(1)
            .createdAt(LocalDateTime.now()).status(BookingStatus.PENDING).bookingDate(dto.getTravelDate()).build();
        booking.setPassengers(buildPassengers(dto.getPassengers(), booking));
        return booking;
    }

    public Booking buildHotelBooking(User user, Hotel hotel, BookingHotelDTO dto, long days) {
        return Booking.builder().user(user).hotel(hotel).bookingType(BookingType.HOTEL)
            .bookingName(dto.getBookingName()).gender(dto.getGender()).units(dto.getUnits()).days((int) days)
            .checkInDate(dto.getCheckInDate()).checkOutDate(dto.getCheckOutDate())
            .amount(hotel.getPrice() * dto.getUnits() * days)
            .status(BookingStatus.PENDING).bookingDate(dto.getCheckInDate()).createdAt(LocalDateTime.now()).build();
    }

    public Booking buildTransportBooking(User user, Transport transport, BookingTransportDTO dto) {
        Booking booking = Booking.builder().user(user).transport(transport).bookingType(BookingType.TRANSPORT)
            .bookingName(dto.getBookingName()).gender(dto.getGender()).units(dto.getUnits())
            .amount(transport.getPrice() * dto.getUnits())
            .status(BookingStatus.PENDING).bookingDate(dto.getTravelDate()).createdAt(LocalDateTime.now()).build();
        booking.setPassengers(buildPassengers(dto.getPassengers(), booking));
        return booking;
    }

    public Booking buildPackageBooking(User user, TravelPackage tpackage, BookingPackageDTO dto) {
        return Booking.builder().user(user).travelPackage(tpackage).bookingType(BookingType.PACKAGE)
            .bookingName(dto.getBookingName()).gender(dto.getGender()).units(dto.getUnits())
            .amount(tpackage.getPrice() * dto.getUnits()).status(BookingStatus.PENDING)
            .bookingDate(tpackage.getStartDate() != null ? tpackage.getStartDate() : LocalDate.now())
            .createdAt(LocalDateTime.now()).build();
    }

    // ---- Response Mappers ----

    public BookingFlightResponseDTO toFlightResponse(Booking b, Flight f, User u, LocalDate travelDate) {
        return BookingFlightResponseDTO.builder()
            .bookingId(b.getBookingId()).bookingType(b.getBookingType()).amount(b.getAmount()).status(b.getStatus())
            .userId(u.getUserId()).email(u.getEmail()).units(b.getUnits()).createdAt(b.getCreatedAt())
            .bookingDate(b.getBookingDate()).travelDate(travelDate).bookingName(b.getBookingName()).gender(b.getGender())
            .flightId(f.getFlightId()).flightNumber(f.getFlightNumber()).source(f.getSource()).destination(f.getDestination())
            .departureTime(f.getDepartureTime()).arrivalTime(f.getArrivalTime())
            .passengers(mapPassengers(b.getPassengers())).build();
    }

    public BookingHotelResponseDTO toHotelResponse(Booking b, Hotel h, User u) {
        return BookingHotelResponseDTO.builder()
            .bookingId(b.getBookingId()).bookingType(b.getBookingType()).amount(b.getAmount()).status(b.getStatus())
            .userId(u.getUserId()).email(u.getEmail()).units(b.getUnits()).days(b.getDays())
            .checkInDate(b.getCheckInDate()).checkOutDate(b.getCheckOutDate())
            .bookingName(b.getBookingName()).gender(b.getGender())
            .hotelId(h.getHotelId()).hotelName(h.getHotelName()).city(h.getCity()).build();
    }

    public BookingTransportResponseDTO toTransportResponse(Booking b, Transport t, User u, LocalDate travelDate) {
        return BookingTransportResponseDTO.builder()
            .bookingId(b.getBookingId()).bookingType(b.getBookingType()).amount(b.getAmount()).status(b.getStatus())
            .bookingDate(b.getBookingDate()).travelDate(travelDate).userId(u.getUserId()).email(u.getEmail())
            .units(b.getUnits()).bookingName(b.getBookingName()).gender(b.getGender())
            .transportId(t.getTransportId()).source(t.getSource()).destination(t.getDestination())
            .transportType(t.getTransportType()).departureTime(t.getDepartureTime()).arrivalTime(t.getArrivalTime())
            .passengers(mapPassengers(b.getPassengers())).build();
    }

    public BookingPackageResponseDTO toPackageResponse(Booking b, TravelPackage tp, User u) {
        return BookingPackageResponseDTO.builder()
            .bookingId(b.getBookingId()).bookingType(b.getBookingType()).amount(b.getAmount()).status(b.getStatus())
            .bookingDate(b.getBookingDate()).userId(u.getUserId()).email(u.getEmail()).units(b.getUnits())
            .bookingName(b.getBookingName()).gender(b.getGender())
            .packageId(tp.getPackageId()).packageName(tp.getPackageName()).source(tp.getSource())
            .destination(tp.getDestination()).durationDays(tp.getDurationDays()).startDate(tp.getStartDate())
            .endDate(tp.getEndDate()).category(tp.getCategory()).packageStatus(tp.getStatus()).build();
    }

    public BookingResponseDTO mapToDTO(Booking b) {
        return BookingResponseDTO.builder()
            .bookingId(b.getBookingId()).bookingType(b.getBookingType()).amount(b.getAmount()).status(b.getStatus())
            .userId(b.getUser().getUserId()).email(b.getUser().getEmail()).units(b.getUnits())
            .flightId(b.getFlight() != null ? b.getFlight().getFlightId() : null)
            .flightNumber(b.getFlight() != null ? b.getFlight().getFlightNumber() : null)
            .hotelId(b.getHotel() != null ? b.getHotel().getHotelId() : null)
            .hotelName(b.getHotel() != null ? b.getHotel().getHotelName() : null)
            .transportId(b.getTransport() != null ? b.getTransport().getTransportId() : null)
            .transportType(b.getTransport() != null ? b.getTransport().getTransportType() : null)
            .packageId(b.getTravelPackage() != null ? b.getTravelPackage().getPackageId() : null)
            .packageName(b.getTravelPackage() != null ? b.getTravelPackage().getPackageName() : null)
            .itineraryId(b.getItinerary() != null ? b.getItinerary().getItineraryId() : null)
            .passengers(b.getPassengers() != null && !b.getPassengers().isEmpty()
                ? mapPassengers(b.getPassengers()) : null).build();
    }
}