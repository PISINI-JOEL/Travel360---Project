package com.cts.serviceimpl;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.*;
import com.cts.enums.Gender;
import com.cts.exception.*;
import com.cts.repository.*;
import com.cts.service.AuditLogService;
import com.cts.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepo;
    @Mock private UserRepository userRepo;
    @Mock private FlightRepository flightRepo;
    @Mock private HotelRepository hotelrepo;
    @Mock private InvoiceRepository invoiceRepo;
    @Mock private TravelPackageRepository packageRepo;
    @Mock private NotificationService notificationService;
    @Mock private TransportRepository transportRepo;
    @Mock private PaymentRepository paymentRepo;
    @Mock private PassengerRepository passengerRepo;
    @Mock private AuthenticatedUserProvider authUser;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private BookingServiceImpl service;

    private User user;
    private Flight flight;
    private Hotel hotel;
    private TravelPackage tpackage;
    private Transport transport;

    @BeforeEach
    void setup() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");

        flight = Flight.builder()
                .flightId(10L)
                .flightNumber("AA-100")
                .source("Chennai")
                .destination("Delhi")
                .status(FlightStatus.SCHEDULED)
                .totalSeats(100)
                .price(5000.0)
                .departureTime(LocalTime.of(8, 0))
                .arrivalTime(LocalTime.of(10, 0))
                .build();

        hotel = Hotel.builder()
                .hotelId(20L)
                .hotelName("Taj")
                .city("Chennai")
                .status(HotelStatus.AVAILABLE)
                .totalRooms(50)
                .price(3000.0)
                .build();

        tpackage = TravelPackage.builder()
                .packageId(30L)
                .packageName("Goa")
                .source("Chennai")
                .destination("Goa")
                .status(PackageStatus.AVAILABLE)
                .totalSlots(50)
                .price(15000.0)
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .durationDays(5)
                .build();

        transport = Transport.builder()
                .transportId(40L)
                .source("Chennai")
                .destination("Bangalore")
                .transportStatus(TransportStatus.AVAILABLE)
                .transportTotalSeats(40)
                .price(800.0)
                .departureTime(LocalTime.of(6, 0))
                .arrivalTime(LocalTime.of(12, 0))
                .build();
    }

    private PassengerDTO buildPassengerDTO() {
        PassengerDTO p = new PassengerDTO();
        p.setPassengerName("John Doe");
        p.setGender(Gender.MALE);
        return p;
    }

    // ---------------- FLIGHT BOOKING ----------------

    @Test
    void createFlightBooking_success() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(10L);
        dto.setTravelDate(LocalDate.now().plusDays(10));
        dto.setUnits(1);
        dto.setBookingName("John");
        dto.setGender(Gender.MALE);
        dto.setPassengers(List.of(buildPassengerDTO()));

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(bookingRepo.getBookedSeats(10L, dto.getTravelDate())).thenReturn(0);
        when(bookingRepo.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(1000L);
            return b;
        });

        BookingFlightResponseDTO response = service.createFlightBooking(dto);

        assertNotNull(response);
        assertEquals(1000L, response.getBookingId());
        verify(auditLogService).logAction(any(), any(), any(), any(), any());
        verify(notificationService).sendNotification(eq(user), anyString(), eq(NotificationCategory.BOOKING));
    }

    @Test
    void createFlightBooking_userNotFound() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(99L);

        when(userRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createFlightBooking(dto));
    }

    @Test
    void createFlightBooking_flightNotFound() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(99L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FlightNotFoundException.class, () -> service.createFlightBooking(dto));
    }

    @Test
    void createFlightBooking_flightNotScheduled() {

        flight.setStatus(FlightStatus.CANCELLED);

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(10L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));

        assertThrows(InvalidBookingException.class, () -> service.createFlightBooking(dto));
    }

    @Test
    void createFlightBooking_insufficientSeats() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(10L);
        dto.setTravelDate(LocalDate.now().plusDays(10));
        dto.setUnits(5);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(bookingRepo.getBookedSeats(10L, dto.getTravelDate())).thenReturn(98);

        assertThrows(InsufficientAvailabilityException.class, () -> service.createFlightBooking(dto));
    }

    @Test
    void createFlightBooking_dateTooClose() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(10L);
        dto.setTravelDate(LocalDate.now());
        dto.setUnits(1);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(bookingRepo.getBookedSeats(10L, dto.getTravelDate())).thenReturn(0);

        assertThrows(InvalidBookingException.class, () -> service.createFlightBooking(dto));
    }

    @Test
    void createFlightBooking_passengerCountMismatch() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(10L);
        dto.setTravelDate(LocalDate.now().plusDays(10));
        dto.setUnits(2);
        dto.setPassengers(List.of(buildPassengerDTO()));

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(bookingRepo.getBookedSeats(anyLong(), any())).thenReturn(0);

        assertThrows(InvalidBookingException.class, () -> service.createFlightBooking(dto));
    }

    // ---------------- HOTEL BOOKING ----------------

    @Test
    void createHotelBooking_success() {

        BookingHotelDTO dto = new BookingHotelDTO();
        dto.setUserId(1L);
        dto.setHotelId(20L);
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(8));
        dto.setUnits(1);
        dto.setBookingName("John");
        dto.setGender(Gender.MALE);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(hotelrepo.findById(20L)).thenReturn(Optional.of(hotel));
        when(bookingRepo.getBookedRooms(eq(20L), any(), any())).thenReturn(0);
        when(bookingRepo.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(2000L);
            return b;
        });

        BookingHotelResponseDTO response = service.createHotelBooking(dto);

        assertNotNull(response);
        assertEquals(2000L, response.getBookingId());
    }

    @Test
    void createHotelBooking_hotelNotFound() {

        BookingHotelDTO dto = new BookingHotelDTO();
        dto.setUserId(1L);
        dto.setHotelId(99L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(hotelrepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () -> service.createHotelBooking(dto));
    }

    @Test
    void createHotelBooking_hotelNotAvailable() {

        hotel.setStatus(HotelStatus.INACTIVE);

        BookingHotelDTO dto = new BookingHotelDTO();
        dto.setUserId(1L);
        dto.setHotelId(20L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(hotelrepo.findById(20L)).thenReturn(Optional.of(hotel));

        assertThrows(InvalidBookingException.class, () -> service.createHotelBooking(dto));
    }

    @Test
    void createHotelBooking_invalidDateRange() {

        BookingHotelDTO dto = new BookingHotelDTO();
        dto.setUserId(1L);
        dto.setHotelId(20L);
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(5));

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(hotelrepo.findById(20L)).thenReturn(Optional.of(hotel));

        assertThrows(InvalidBookingException.class, () -> service.createHotelBooking(dto));
    }

    @Test
    void createHotelBooking_insufficientRooms() {

        BookingHotelDTO dto = new BookingHotelDTO();
        dto.setUserId(1L);
        dto.setHotelId(20L);
        dto.setCheckInDate(LocalDate.now().plusDays(5));
        dto.setCheckOutDate(LocalDate.now().plusDays(7));
        dto.setUnits(5);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(hotelrepo.findById(20L)).thenReturn(Optional.of(hotel));
        when(bookingRepo.getBookedRooms(eq(20L), any(), any())).thenReturn(48);

        assertThrows(InsufficientAvailabilityException.class, () -> service.createHotelBooking(dto));
    }

    // ---------------- PACKAGE BOOKING ----------------

    @Test
    void createPackageBooking_success() {

        BookingPackageDTO dto = new BookingPackageDTO();
        dto.setUserId(1L);
        dto.setPackageId(30L);
        dto.setUnits(1);
        dto.setBookingName("John");
        dto.setGender(Gender.MALE);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(packageRepo.findById(30L)).thenReturn(Optional.of(tpackage));
        when(bookingRepo.getBookedSlots(30L)).thenReturn(0);
        when(bookingRepo.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(3000L);
            return b;
        });

        BookingPackageResponseDTO response = service.createPackageBooking(dto);

        assertNotNull(response);
        assertEquals(3000L, response.getBookingId());
    }

    @Test
    void createPackageBooking_packageNotFound() {

        BookingPackageDTO dto = new BookingPackageDTO();
        dto.setUserId(1L);
        dto.setPackageId(99L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(packageRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PackageNotFoundException.class, () -> service.createPackageBooking(dto));
    }

    @Test
    void createPackageBooking_packageNotAvailable() {

        tpackage.setStatus(PackageStatus.INACTIVE);

        BookingPackageDTO dto = new BookingPackageDTO();
        dto.setUserId(1L);
        dto.setPackageId(30L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(packageRepo.findById(30L)).thenReturn(Optional.of(tpackage));

        assertThrows(InvalidBookingException.class, () -> service.createPackageBooking(dto));
    }

    @Test
    void createPackageBooking_insufficientSlots() {

        BookingPackageDTO dto = new BookingPackageDTO();
        dto.setUserId(1L);
        dto.setPackageId(30L);
        dto.setUnits(10);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(packageRepo.findById(30L)).thenReturn(Optional.of(tpackage));
        when(bookingRepo.getBookedSlots(30L)).thenReturn(45);

        assertThrows(InsufficientAvailabilityException.class, () -> service.createPackageBooking(dto));
    }

    // ---------------- TRANSPORT BOOKING ----------------

    @Test
    void createTransportBooking_success() {

        BookingTransportDTO dto = new BookingTransportDTO();
        dto.setUserId(1L);
        dto.setTransportId(40L);
        dto.setTravelDate(LocalDate.now().plusDays(10));
        dto.setUnits(1);
        dto.setBookingName("John");
        dto.setGender(Gender.MALE);
        dto.setPassengers(List.of(buildPassengerDTO()));

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transportRepo.findById(40L)).thenReturn(Optional.of(transport));
        when(bookingRepo.getBookedTransportSeats(40L, dto.getTravelDate())).thenReturn(0);
        when(bookingRepo.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(4000L);
            return b;
        });

        BookingTransportResponseDTO response = service.createTransportBooking(dto);

        assertNotNull(response);
        assertEquals(4000L, response.getBookingId());
    }

    @Test
    void createTransportBooking_transportNotFound() {

        BookingTransportDTO dto = new BookingTransportDTO();
        dto.setUserId(1L);
        dto.setTransportId(99L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transportRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransportNotFoundException.class, () -> service.createTransportBooking(dto));
    }

    @Test
    void createTransportBooking_transportNotAvailable() {

        transport.setTransportStatus(TransportStatus.OUT_OF_SERVICE);

        BookingTransportDTO dto = new BookingTransportDTO();
        dto.setUserId(1L);
        dto.setTransportId(40L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transportRepo.findById(40L)).thenReturn(Optional.of(transport));

        assertThrows(InvalidBookingException.class, () -> service.createTransportBooking(dto));
    }

    @Test
    void createTransportBooking_dateTooClose() {

        BookingTransportDTO dto = new BookingTransportDTO();
        dto.setUserId(1L);
        dto.setTransportId(40L);
        dto.setTravelDate(LocalDate.now());

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transportRepo.findById(40L)).thenReturn(Optional.of(transport));

        assertThrows(InvalidBookingException.class, () -> service.createTransportBooking(dto));
    }

    @Test
    void createTransportBooking_insufficientSeats() {

        BookingTransportDTO dto = new BookingTransportDTO();
        dto.setUserId(1L);
        dto.setTransportId(40L);
        dto.setTravelDate(LocalDate.now().plusDays(10));
        dto.setUnits(5);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(transportRepo.findById(40L)).thenReturn(Optional.of(transport));
        when(bookingRepo.getBookedTransportSeats(40L, dto.getTravelDate())).thenReturn(38);

        assertThrows(InsufficientAvailabilityException.class, () -> service.createTransportBooking(dto));
    }

    // ---------------- LISTS ----------------

    @Test
    void getBookingsByUser_success() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(100.0)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.PENDING).units(1).build();

        when(bookingRepo.findByUserUserId(1L)).thenReturn(List.of(b));

        List<BookingResponseDTO> list = service.getBookingsByUser(1L);

        assertEquals(1, list.size());
        verify(authUser).assertCanActAs(1L);
    }

    @Test
    void getAllBookings_success() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(100.0)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.PENDING).units(1).build();

        when(bookingRepo.findAll()).thenReturn(List.of(b));

        List<BookingResponseDTO> list = service.getAllBookings();

        assertEquals(1, list.size());
    }

    // ---------------- DELETE BOOKING ----------------

    @Test
    void deleteBooking_pendingSuccess() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.PENDING)
                .bookingDate(LocalDate.now().plusDays(10)).build();

        Invoice inv = Invoice.builder().invoiceId(1L).booking(b).status(PaymentStatus.PENDING).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(invoiceRepo.findByBookingBookingId(1L)).thenReturn(List.of(inv));

        BookingCancelResponseDTO response = service.deleteBooking(dto);

        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        assertEquals(0.0, response.getRefundAmount());
        verify(invoiceRepo).save(any());
    }

    @Test
    void deleteBooking_confirmedFullRefund() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now().plusDays(15)).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        BookingCancelResponseDTO response = service.deleteBooking(dto);

        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        assertEquals(1000.0, response.getRefundAmount());
        assertEquals("FULL", response.getRefundStatus());
    }

    @Test
    void deleteBooking_confirmedPartialRefund() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now().plusDays(5)).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        BookingCancelResponseDTO response = service.deleteBooking(dto);

        assertEquals("PARTIAL", response.getRefundStatus());
        assertEquals(800.0, response.getRefundAmount());
    }

    @Test
    void deleteBooking_bookingNotFound() {

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(99L);

        when(bookingRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteBooking(dto));
    }

    @Test
    void deleteBooking_alreadyCancelled() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.CANCELLED).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        assertThrows(InvalidBookingException.class, () -> service.deleteBooking(dto));
    }

    // ---------------- CANCEL PASSENGER ----------------

    @Test
    void cancelPassenger_multiPassengerSuccess() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(2000.0).units(2)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.PENDING)
                .bookingDate(LocalDate.now().plusDays(10)).build();

        Passenger p = Passenger.builder().passengerId(5L).passengerName("John")
                .status(PassengerStatus.ACTIVE).booking(b).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));
        when(passengerRepo.countByBookingBookingIdAndStatus(1L, PassengerStatus.ACTIVE)).thenReturn(2L);
        when(invoiceRepo.findByBookingBookingId(1L)).thenReturn(new ArrayList<>());

        PassengerCancelResponseDTO response = service.cancelPassenger(1L, 5L, 1L);

        assertNotNull(response);
        assertEquals(5L, response.getPassengerId());
        assertEquals(1, response.getRemainingUnits());
    }

    @Test
    void cancelPassenger_lastPassengerCancelsBooking() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.PENDING)
                .bookingDate(LocalDate.now().plusDays(10)).build();

        Passenger p = Passenger.builder().passengerId(5L).passengerName("John")
                .status(PassengerStatus.ACTIVE).booking(b).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));
        when(passengerRepo.countByBookingBookingIdAndStatus(1L, PassengerStatus.ACTIVE)).thenReturn(1L);
        when(invoiceRepo.findByBookingBookingId(1L)).thenReturn(new ArrayList<>());

        PassengerCancelResponseDTO response = service.cancelPassenger(1L, 5L, 1L);

        assertEquals(BookingStatus.CANCELLED, response.getBookingStatus());
        assertEquals(0, response.getRemainingUnits());
    }

    @Test
    void cancelPassenger_bookingNotFound() {

        when(bookingRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelPassenger(99L, 5L, 1L));
    }

    @Test
    void cancelPassenger_bookingAlreadyCancelled() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.CANCELLED).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        assertThrows(InvalidBookingException.class,
                () -> service.cancelPassenger(1L, 5L, 1L));
    }

    @Test
    void cancelPassenger_wrongBookingType() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.PENDING)
                .bookingType(BookingType.HOTEL).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        assertThrows(InvalidBookingException.class,
                () -> service.cancelPassenger(1L, 5L, 1L));
    }

    @Test
    void cancelPassenger_passengerNotFound() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.PENDING)
                .bookingType(BookingType.FLIGHT).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancelPassenger(1L, 5L, 1L));
    }

    @Test
    void cancelPassenger_passengerNotInBooking() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.PENDING)
                .bookingType(BookingType.FLIGHT).build();

        Booking other = Booking.builder().bookingId(2L).build();
        Passenger p = Passenger.builder().passengerId(5L).status(PassengerStatus.ACTIVE).booking(other).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));

        assertThrows(InvalidBookingException.class,
                () -> service.cancelPassenger(1L, 5L, 1L));
    }

    @Test
    void cancelPassenger_passengerAlreadyCancelled() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.PENDING)
                .bookingType(BookingType.FLIGHT).build();

        Passenger p = Passenger.builder().passengerId(5L).status(PassengerStatus.CANCELLED).booking(b).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));

        assertThrows(InvalidBookingException.class,
                () -> service.cancelPassenger(1L, 5L, 1L));
    }

    // ---------------- ADDITIONAL COVERAGE ----------------

    // validatePassengerCount: passengers == null branch
    @Test
    void createFlightBooking_passengersNull() {

        BookingFlightDTO dto = new BookingFlightDTO();
        dto.setUserId(1L);
        dto.setFlightId(10L);
        dto.setTravelDate(LocalDate.now().plusDays(10));
        dto.setUnits(1);
        dto.setPassengers(null);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(flightRepo.findById(10L)).thenReturn(Optional.of(flight));
        when(bookingRepo.getBookedSeats(10L, dto.getTravelDate())).thenReturn(0);

        assertThrows(InvalidBookingException.class, () -> service.createFlightBooking(dto));
    }

    // createPackageBooking: package startDate == null -> bookingDate falls back to today
    @Test
    void createPackageBooking_nullStartDate() {

        tpackage.setStartDate(null);

        BookingPackageDTO dto = new BookingPackageDTO();
        dto.setUserId(1L);
        dto.setPackageId(30L);
        dto.setUnits(1);
        dto.setBookingName("John");
        dto.setGender(Gender.MALE);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(packageRepo.findById(30L)).thenReturn(Optional.of(tpackage));
        when(bookingRepo.getBookedSlots(30L)).thenReturn(0);
        when(bookingRepo.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setBookingId(3001L);
            return b;
        });

        BookingPackageResponseDTO response = service.createPackageBooking(dto);

        assertNotNull(response);
        assertEquals(LocalDate.now(), response.getBookingDate());
    }

    // mapToDTO: all relations populated (flight, hotel, transport, package, itinerary, passengers)
    @Test
    void getAllBookings_mapsAllRelations() {

        Passenger p = Passenger.builder().passengerId(9L).passengerName("P")
                .status(PassengerStatus.ACTIVE).build();

        Booking b = Booking.builder().bookingId(1L).user(user).amount(100.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.PENDING)
                .flight(flight).hotel(hotel).transport(transport).travelPackage(tpackage)
                .itinerary(Itinerary.builder().itineraryId(7L).build())
                .passengers(List.of(p)).build();

        when(bookingRepo.findAll()).thenReturn(List.of(b));

        List<BookingResponseDTO> list = service.getAllBookings();

        assertEquals(1, list.size());
        BookingResponseDTO dto = list.get(0);
        assertEquals(flight.getFlightId(), dto.getFlightId());
        assertEquals(hotel.getHotelId(), dto.getHotelId());
        assertEquals(transport.getTransportId(), dto.getTransportId());
        assertEquals(tpackage.getPackageId(), dto.getPackageId());
        assertEquals(7L, dto.getItineraryId());
        assertNotNull(dto.getPassengers());
        assertEquals(1, dto.getPassengers().size());
    }

    // deleteBooking: status neither PENDING/CONFIRMED/CANCELLED -> invalid state
    @Test
    void deleteBooking_invalidState() {

        Booking b = Booking.builder().bookingId(1L).user(user).status(BookingStatus.FAILED).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        assertThrows(InvalidBookingException.class, () -> service.deleteBooking(dto));
    }

    // deleteBooking CONFIRMED: refund 0 (booking date today) -> refundStatus NONE
    @Test
    void deleteBooking_confirmedNoRefund() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now()).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        BookingCancelResponseDTO response = service.deleteBooking(dto);

        assertEquals("NONE", response.getRefundStatus());
        assertEquals(0.0, response.getRefundAmount());
    }

    // deleteBooking CONFIRMED: 2-3 days before -> 60% refund (else branch)
    @Test
    void deleteBooking_confirmedRefund60() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now().plusDays(3)).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        BookingCancelResponseDTO response = service.deleteBooking(dto);

        assertEquals("PARTIAL", response.getRefundStatus());
        assertEquals(600.0, response.getRefundAmount());
    }

    // calculateRefundAmount: exactly 1 day before -> throws
    @Test
    void deleteBooking_confirmedOneDayBefore_throws() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now().plusDays(1)).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        assertThrows(InvalidBookingException.class, () -> service.deleteBooking(dto));
    }

    // calculateRefundAmount: null booking date -> throws
    @Test
    void deleteBooking_confirmedNullBookingDate_throws() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(1000.0).units(1)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(null).build();

        BookingCancelDTO dto = new BookingCancelDTO();
        dto.setBookingId(1L);
        dto.setUserId(1L);

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));

        assertThrows(InvalidBookingException.class, () -> service.deleteBooking(dto));
    }

    // cancelPassenger CONFIRMED, full per-seat refund + pending invoice sync + refund invoice/payment
    @Test
    void cancelPassenger_confirmedFullRefund() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(2000.0).units(2)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now().plusDays(15)).build();

        Passenger p = Passenger.builder().passengerId(5L).passengerName("John")
                .status(PassengerStatus.ACTIVE).booking(b).build();

        Invoice pendingInvoice = Invoice.builder().invoiceId(1L).booking(b)
                .status(PaymentStatus.PENDING).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));
        when(passengerRepo.countByBookingBookingIdAndStatus(1L, PassengerStatus.ACTIVE)).thenReturn(2L);
        when(invoiceRepo.findByBookingBookingId(1L)).thenReturn(List.of(pendingInvoice));

        PassengerCancelResponseDTO response = service.cancelPassenger(1L, 5L, 1L);

        assertEquals("FULL", response.getRefundStatus());
        assertEquals(1000.0, response.getRefundAmount());
        assertEquals(1, response.getRemainingUnits());
        verify(paymentRepo).save(any());
    }

    // cancelPassenger CONFIRMED, partial per-seat refund
    @Test
    void cancelPassenger_confirmedPartialRefund() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(2000.0).units(2)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now().plusDays(5)).build();

        Passenger p = Passenger.builder().passengerId(5L).passengerName("John")
                .status(PassengerStatus.ACTIVE).booking(b).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));
        when(passengerRepo.countByBookingBookingIdAndStatus(1L, PassengerStatus.ACTIVE)).thenReturn(2L);
        when(invoiceRepo.findByBookingBookingId(1L)).thenReturn(new ArrayList<>());

        PassengerCancelResponseDTO response = service.cancelPassenger(1L, 5L, 1L);

        assertEquals("PARTIAL", response.getRefundStatus());
        assertEquals(800.0, response.getRefundAmount());
    }

    // cancelPassenger CONFIRMED, no refund (travel date today) -> refundStatus NONE, no refund payment
    @Test
    void cancelPassenger_confirmedNoRefund() {

        Booking b = Booking.builder().bookingId(1L).user(user).amount(2000.0).units(2)
                .bookingType(BookingType.FLIGHT).status(BookingStatus.CONFIRMED)
                .bookingDate(LocalDate.now()).build();

        Passenger p = Passenger.builder().passengerId(5L).passengerName("John")
                .status(PassengerStatus.ACTIVE).booking(b).build();

        when(bookingRepo.findById(1L)).thenReturn(Optional.of(b));
        when(passengerRepo.findById(5L)).thenReturn(Optional.of(p));
        when(passengerRepo.countByBookingBookingIdAndStatus(1L, PassengerStatus.ACTIVE)).thenReturn(2L);
        when(invoiceRepo.findByBookingBookingId(1L)).thenReturn(new ArrayList<>());

        PassengerCancelResponseDTO response = service.cancelPassenger(1L, 5L, 1L);

        assertEquals("NONE", response.getRefundStatus());
        assertEquals(0.0, response.getRefundAmount());
        assertEquals(1, response.getRemainingUnits());
        verify(paymentRepo, never()).save(any());
    }
}
