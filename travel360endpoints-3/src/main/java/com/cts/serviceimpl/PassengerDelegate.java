package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.*;
import com.cts.exception.*;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassengerDelegate {

    private final BookingRepository bookingRepo;
    private final PassengerRepository passengerRepo;
    private final BookingCancelDelegate bookingCancelDelegate;
    private final BookingHelper helper;
    private final AuthenticatedUserProvider authUser;

    @Transactional
    public PassengerCancelResponseDTO cancelPassenger(Long bookingId, Long passengerId, Long userId) {
        log.info("Cancelling passengerId: {} from bookingId: {}", passengerId, bookingId);
        Booking booking = bookingRepo.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        authUser.assertCanActAs(booking.getUser().getUserId());

        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new InvalidBookingException("Booking is already cancelled");
        if (booking.getBookingType() != BookingType.FLIGHT && booking.getBookingType() != BookingType.TRANSPORT)
            throw new InvalidBookingException("Passenger cancellation only allowed for flight and transport bookings");

        Passenger passenger = fetchPassenger(passengerId, bookingId);
        long activeCount = passengerRepo.countByBookingBookingIdAndStatus(bookingId, PassengerStatus.ACTIVE);

        if (activeCount <= 1)
            return cancelLast(booking, passenger, userId, bookingId, passengerId);

        return cancelSingle(booking, passenger, bookingId, passengerId);
    }

    private Passenger fetchPassenger(Long passengerId, Long bookingId) {
        Passenger passenger = passengerRepo.findById(passengerId)
            .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));
        if (passenger.getBooking() == null || !passenger.getBooking().getBookingId().equals(bookingId))
            throw new InvalidBookingException("Passenger does not belong to the given booking");
        if (passenger.getStatus() == PassengerStatus.CANCELLED)
            throw new InvalidBookingException("Passenger is already cancelled");
        return passenger;
    }

    private PassengerCancelResponseDTO cancelLast(Booking booking, Passenger passenger,
            Long userId, Long bookingId, Long passengerId) {
        BookingCancelDTO cancelDto = new BookingCancelDTO();
        cancelDto.setBookingId(bookingId);
        cancelDto.setUserId(userId);
        BookingCancelResponseDTO full = bookingCancelDelegate.deleteBooking(cancelDto);
        passenger.setStatus(PassengerStatus.CANCELLED);
        passengerRepo.save(passenger);
        return PassengerCancelResponseDTO.builder()
            .bookingId(bookingId).passengerId(passengerId).passengerName(passenger.getPassengerName())
            .bookingStatus(BookingStatus.CANCELLED).remainingUnits(0)
            .refundAmount(full.getRefundAmount()).deductionAmount(full.getDeductionAmount())
            .refundStatus(full.getRefundStatus()).cancelledAt(LocalDateTime.now())
            .message("Last passenger removed; entire booking cancelled").build();
    }

    private PassengerCancelResponseDTO cancelSingle(Booking booking, Passenger passenger,
            Long bookingId, Long passengerId) {
        double perSeat = booking.getAmount() / booking.getUnits();
        double refund = booking.getStatus() == BookingStatus.CONFIRMED
            ? helper.calculateRefundAmount(perSeat, booking.getBookingDate()) : 0.0;
        String refundStatus = helper.resolveRefundStatus(refund, perSeat);

        booking.setUnits(booking.getUnits() - 1);
        booking.setAmount(booking.getAmount() - perSeat);
        bookingRepo.save(booking);
        helper.adjustPendingInvoice(bookingId, booking.getAmount());

        passenger.setStatus(PassengerStatus.CANCELLED);
        passengerRepo.save(passenger);

        if (refund > 0) helper.createRefundInvoiceAndPayment(booking, refund);
        helper.notify(booking.getUser(), "Passenger " + passenger.getPassengerName()
            + " removed from booking " + bookingId + ". Refund: " + refund);

        return PassengerCancelResponseDTO.builder()
            .bookingId(bookingId).passengerId(passengerId).passengerName(passenger.getPassengerName())
            .bookingStatus(booking.getStatus()).remainingUnits(booking.getUnits())
            .refundAmount(refund).deductionAmount(perSeat - refund)
            .refundStatus(refundStatus).cancelledAt(LocalDateTime.now())
            .message("Passenger removed from booking").build();
    }
}