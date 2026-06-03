package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.TransportStatus;
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
public class TransportBookingDelegate {

    private final TransportRepository transportRepo;
    private final BookingRepository bookingRepo;
    private final BookingHelper helper;

    @Transactional
    public BookingTransportResponseDTO createTransportBooking(BookingTransportDTO dto) {
        log.info("Creating transport booking userId: {}, transportId: {}", dto.getUserId(), dto.getTransportId());
        User user = helper.fetchUser(dto.getUserId());
        Transport transport = resolveAndValidate(dto);
        Booking booking = helper.buildTransportBooking(user, transport, dto);
        bookingRepo.save(booking);
        helper.createPendingInvoice(booking);
        helper.notify(user, "Transport booked from " + transport.getSource() + " to " + transport.getDestination());
        return helper.toTransportResponse(booking, transport, user, dto.getTravelDate());
    }

    private Transport resolveAndValidate(BookingTransportDTO dto) {
        Transport transport = transportRepo.findById(dto.getTransportId())
            .orElseThrow(() -> new TransportNotFoundException("Transport not found"));
        if (transport.getTransportStatus() != TransportStatus.AVAILABLE)
            throw new InvalidBookingException("Transport is not available for booking");
        if (!dto.getTravelDate().isAfter(LocalDate.now().plusDays(1)))
            throw new InvalidBookingException("Booking not allowed 1 day before or on the same day");
        if (transport.getTransportTotalSeats() - bookingRepo.getBookedTransportSeats(transport.getTransportId(), dto.getTravelDate()) < dto.getUnits())
            throw new InsufficientAvailabilityException("Not enough seats available");
        helper.validatePassengerCount(dto.getPassengers(), dto.getUnits());
        return transport;
    }
}