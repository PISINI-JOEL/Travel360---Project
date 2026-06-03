package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.FlightStatus;
import com.cts.exception.*;
import com.cts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class FlightBookingDelegate {

    private final FlightRepository flightRepo;
    private final BookingRepository bookingRepo;
    private final BookingHelper helper;

    @Transactional
    public BookingFlightResponseDTO createFlightBooking(BookingFlightDTO dto) {
        log.info("Creating flight booking userId: {}, flightId: {}", dto.getUserId(), dto.getFlightId());
        User user = helper.fetchUser(dto.getUserId());
        Flight flight = resolveAndValidate(dto);
        Booking booking = helper.buildFlightBooking(user, flight, dto);
        bookingRepo.save(booking);
        helper.createPendingInvoice(booking);
        helper.notify(user, "Flight booked successfully. Booking ID: " + booking.getBookingId());
        return helper.toFlightResponse(booking, flight, user, dto.getTravelDate());
    }

    private Flight resolveAndValidate(BookingFlightDTO dto) {
        Flight flight = flightRepo.findById(dto.getFlightId())
            .orElseThrow(() -> new FlightNotFoundException("Flight not found"));
        if (flight.getStatus() != FlightStatus.SCHEDULED)
            throw new InvalidBookingException("Flight is not available for booking");
        if (flight.getTotalSeats() - bookingRepo.getBookedSeats(flight.getFlightId(), dto.getTravelDate()) < dto.getUnits())
            throw new InsufficientAvailabilityException("Not enough seats available");
        if (!dto.getTravelDate().isAfter(LocalDate.now().plusDays(1)))
            throw new InvalidBookingException("Booking not allowed 1 day before or on the same day");
        helper.validatePassengerCount(dto.getPassengers(), dto.getUnits());
        return flight;
    }
}