package com.cts.serviceimpl;

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
import com.cts.entity.*;
import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.HotelStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.PaymentStatus;
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
import java.util.List;

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
   

    @Override
    @Transactional
       public BookingFlightResponseDTO createFlightBooking(BookingFlightDTO dto) {

           User user = userRepo.findById(dto.getUserId())
                   .orElseThrow(() -> new UserNotFoundException("User not found"));

           Flight flight = flightRepo.findById(dto.getFlightId())
                   .orElseThrow(() -> new FlightNotFoundException("Flight not found"));
           
           int totalSeats = flight.getTotalSeats();
           int bookedSeats = bookingRepo.getBookedSeats(flight.getFlightId(),dto.getFlightDate());
           int availableSeats = totalSeats - bookedSeats;
           if (availableSeats < dto.getUnits()) {
        	    throw new InsufficientAvailabilityException("Not enough seats available");
        	}
           if (dto.getFlightDate() == null ||
        		    !dto.getFlightDate().equals(flight.getFlightDate())) {

        		    throw new InvalidBookingException("Booking info not valid");
        		}
        	

           LocalDate today = LocalDate.now();
           LocalDate flightDate = flight.getFlightDate();

           
           if (!flightDate.isAfter(today.plusDays(1))) {
               throw new InvalidBookingException(
                   "Booking is not allowed 1 day before or on the same day of the flight"
               );
           }
  



           Booking booking = Booking.builder()
                   .user(user)
                   .flight(flight)
                   .bookingType(BookingType.FLIGHT)
                   .bookingName(dto.getBookingName())
                   .gender(dto.getGender())
                   .amount(flight.getPrice() * dto.getUnits())
                   .units(dto.getUnits())
                   .days(1)
                   .createdAt(LocalDateTime.now())
                   .status(BookingStatus.CONFIRMED)
                   .bookingDate(flight.getFlightDate())
                   .build();

           bookingRepo.save(booking);
          // flightRepo.save(flight);
          

           Invoice invoice = Invoice.builder()
        	        .booking(booking)                     
        	        .invoiceDate(LocalDateTime.now())     
        	        .amount(booking.getAmount())          
        	        .status(PaymentStatus.PENDING)                    
        	        .build();

        	invoiceRepo.save(invoice);
        	notificationService.sendNotification(
        	        user,
        	        "Flight booked successfully. Booking ID: " + booking.getBookingId(),
        	        NotificationCategory.BOOKING
        	);


           return BookingFlightResponseDTO.builder()
                   .bookingId(booking.getBookingId())
                   .bookingType(booking.getBookingType())
                   .amount(booking.getAmount())
                   .status(booking.getStatus())
                   .userId(user.getUserId())
                   .email(user.getEmail())
                   .units(dto.getUnits())
                   .createdAt(booking.getCreatedAt())
                   .bookingDate(booking.getBookingDate())
                   .arrivalTime(flight.getArrivalTime())
                   .departureTime(flight.getDepartureTime())
                   .flightDate(flight.getFlightDate())
                   .bookingName(booking.getBookingName())
                   .gender(booking.getGender())
                   .flightId(flight.getFlightId())
                   .flightNumber(flight.getFlightNumber())
                   .source(flight.getSource())
                   .destination(flight.getDestination())
                   .build();
       }

       @Override
       @Transactional
       public BookingHotelResponseDTO createHotelBooking(BookingHotelDTO dto) {

           User user = userRepo.findById(dto.getUserId())
                   .orElseThrow(() -> new UserNotFoundException("User not found"));

           Hotel hotel = hotelrepo.findById(dto.getHotelId())
                   .orElseThrow(() -> new HotelNotFoundException("Hotel not found"));
           
           int totalRooms = hotel.getTotalRooms(); 

           int bookedRooms = bookingRepo.getBookedRooms(hotel.getHotelId());

           int availableRooms = totalRooms - bookedRooms;

           if (availableRooms < dto.getUnits()) {
               throw new InsufficientAvailabilityException("Not enough rooms available");
           }
           long days = java.time.temporal.ChronoUnit.DAYS.between(
        	        dto.getCheckInDate(),
        	        dto.getCheckOutDate()
        	);
           if (days <= 0) {
        	    throw new InvalidBookingException("Check-out date must be after check-in date");
        	}
           
           if (hotel.getStatus() != HotelStatus.AVAILABLE) {
        	    throw new InvalidBookingException("Hotel is not available for booking");
        	}

           Booking booking = Booking.builder()
        	        .user(user)
        	        .hotel(hotel)
        	        .bookingType(BookingType.HOTEL)
        	        .bookingName(dto.getBookingName())
        	        .gender(dto.getGender())
        	        .units(dto.getUnits())
        	        .days((int) days)
        	        .checkInDate(dto.getCheckInDate())
        	        .checkOutDate(dto.getCheckOutDate())
        	        .amount(hotel.getPrice() * dto.getUnits() * days)
        	        .status(BookingStatus.CONFIRMED)
        	        .bookingDate(LocalDate.now())
        	        .build();

           

           bookingRepo.save(booking);
           Invoice invoice = Invoice.builder()
       	        .booking(booking)                     
       	        .invoiceDate(LocalDateTime.now())     
       	        .amount(booking.getAmount())          
       	        .status(PaymentStatus.PENDING)                    
       	        .build();

       	invoiceRepo.save(invoice);
       	notificationService.sendNotification(
       	        user,
       	        "Hotel booked successfully. Booking ID: " + booking.getBookingId(),
       	        NotificationCategory.BOOKING
       	);

           return BookingHotelResponseDTO.builder()
                   .bookingId(booking.getBookingId())
                   .bookingType(booking.getBookingType())
                   .amount(booking.getAmount())
                   .status(booking.getStatus())
                   .userId(user.getUserId())
                   .email(user.getEmail())
                   .units(dto.getUnits())
                   .days(booking.getDays())
                   .checkInDate(booking.getCheckInDate())
                   .checkOutDate(booking.getCheckOutDate())
                   .bookingName(booking.getBookingName())
                   .gender(booking.getGender())
                   .hotelId(hotel.getHotelId())
                   .hotelName(hotel.getHotelName())
                   .city(hotel.getCity())
                   .build();
       }

       @Override
       @Transactional
       public BookingPackageResponseDTO createPackageBooking(BookingPackageDTO dto) {

           User user = userRepo.findById(dto.getUserId())
                   .orElseThrow(() -> new UserNotFoundException("User not found"));

           TravelPackage tpackage = packageRepo.findById(dto.getPackageId())
                   .orElseThrow(() -> new PackageNotFoundException("Package not found"));

           Booking booking = Booking.builder()
                   .user(user)
                   .travelPackage(tpackage)
                   .bookingType(BookingType.PACKAGE)
                   .bookingName(dto.getBookingName())
                   .gender(dto.getGender())
                   .units(dto.getUnits())
                   .days(tpackage.getDurationDays())
                   .amount(tpackage.getPrice() * dto.getUnits())
                   .status(BookingStatus.CONFIRMED)
                   .bookingDate(LocalDate.now())
                   .build();

           booking = bookingRepo.save(booking);

          
           Invoice invoice = Invoice.builder()
                   .booking(booking)
                   .invoiceDate(LocalDateTime.now())
                   .amount(booking.getAmount())
                   .status(PaymentStatus.PENDING)
                   .build();

           invoiceRepo.save(invoice);
           notificationService.sendNotification(
        	        user,
        	        "Package booked successfully. Booking ID: " + booking.getBookingId(),
        	        NotificationCategory.BOOKING
        	);

           return BookingPackageResponseDTO.builder()
                   .bookingId(booking.getBookingId())
                   .bookingType(booking.getBookingType())
                   .amount(booking.getAmount())
                   .status(booking.getStatus())

                   .userId(user.getUserId())
                   .email(user.getEmail())
                   .units(dto.getUnits())

                   .bookingName(booking.getBookingName())
                   .gender(booking.getGender())

                   .packageId(tpackage.getPackageId())
                   .packageName(tpackage.getPackageName())
                   .destination(tpackage.getDestination())

                   .build();
       }
       
       
       @Override
       @Transactional
       public BookingTransportResponseDTO createTransportBooking(BookingTransportDTO dto) {

          
           User user = userRepo.findById(dto.getUserId())
                   .orElseThrow(() -> new UserNotFoundException("User not found"));

           
           Transport transport = transportRepo.findById(dto.getTransportId())
                   .orElseThrow(() -> new TransportNotFoundException("Transport not found"));

           
           if (transport.getTransportAvailableSeats() < dto.getUnits()) {
               throw new InsufficientAvailabilityException("Not enough seats available");
           }

          
           transport.setTransportAvailableSeats(
                   transport.getTransportAvailableSeats() - dto.getUnits()
           );

           
           Booking booking = Booking.builder()
                   .user(user)
                   .transport(transport)
                   .bookingType(BookingType.TRANSPORT)
                   .bookingName(dto.getBookingName())
                   .gender(dto.getGender())
                   .units(dto.getUnits())
                   .amount(transport.getPrice() * dto.getUnits())
                   .status(BookingStatus.CONFIRMED)
                   .bookingDate(LocalDate.now())
                   .build();

           booking = bookingRepo.save(booking);

           
           Invoice invoice = Invoice.builder()
                   .booking(booking)
                   .invoiceDate(LocalDateTime.now())
                   .amount(booking.getAmount())
                   .status(PaymentStatus.PENDING)
                   .build();

           invoiceRepo.save(invoice);

           
           notificationService.sendNotification(
                   user,
                   "Transport booked from " + transport.getSource() +
                   " to " + transport.getDestination(),
                   NotificationCategory.BOOKING
           );

          
           return BookingTransportResponseDTO.builder()
                   .bookingId(booking.getBookingId())
                   .bookingType(booking.getBookingType())
                   .amount(booking.getAmount())
                   .status(booking.getStatus())

                   .userId(user.getUserId())
                   .email(user.getEmail())
                   .units(dto.getUnits())

                   .bookingName(booking.getBookingName())
                   .gender(booking.getGender())

                   .transportId(transport.getTransportId())
                   .source(transport.getSource())
                   .destination(transport.getDestination())
                   .transportType(transport.getTransportType())

                   .build();
       }

   
    @Override
    public List<BookingResponseDTO> getBookingsByUser(Long userId) {

        List<Booking> list = bookingRepo.findByUserUserId(userId);

        return list.stream()
                .map(this::mapToDTO)
                .toList();
    }

    
    @Override
    public List<BookingResponseDTO> getAllBookings() {

        List<Booking> list = bookingRepo.findAll();

        return list.stream()
                .map(this::mapToDTO)
                .toList();
    }

    
    private BookingResponseDTO mapToDTO(Booking booking) {

        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .bookingType(booking.getBookingType())
                .amount(booking.getAmount())
                .status(booking.getStatus())

                .userId(booking.getUser().getUserId())
                .email(booking.getUser().getEmail())
                .units(booking.getUnits())

               
                .flightId(booking.getFlight() != null
                        ? booking.getFlight().getFlightId()
                        : null)

                .flightNumber(booking.getFlight() != null
                        ? booking.getFlight().getFlightNumber()
                        : null)

                
                .hotelId(booking.getHotel() != null
                        ? booking.getHotel().getHotelId()
                        : null)

                .hotelName(booking.getHotel() != null
                        ? booking.getHotel().getHotelName()
                        : null)

                
                .transportId(booking.getTransport() != null
                        ? booking.getTransport().getTransportId()
                        : null)

                .transportType(booking.getTransport() != null
                        ? booking.getTransport().getTransportType()
                        : null)

               

               
                .packageId(booking.getTravelPackage() != null
                        ? booking.getTravelPackage().getPackageId()
                        : null)

                .packageName(booking.getTravelPackage() != null
                        ? booking.getTravelPackage().getPackageName()
                        : null)

                
                .build();
    }
    
    private void createInvoice(Booking booking) {
        Invoice invoice = Invoice.builder()
                .booking(booking)
                .invoiceDate(LocalDateTime.now())
                .amount(booking.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        invoiceRepo.save(invoice);
    }
    
    
}