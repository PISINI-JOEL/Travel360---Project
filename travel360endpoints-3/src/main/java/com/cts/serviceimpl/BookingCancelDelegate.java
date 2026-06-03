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
public class BookingCancelDelegate {

    private final BookingRepository bookingRepo;
    private final InvoiceRepository invoiceRepo;
    private final BookingHelper helper;
    private final AuthenticatedUserProvider authUser;

    @Transactional
    public BookingCancelResponseDTO deleteBooking(BookingCancelDTO dto) {
        log.info("Cancelling bookingId: {}", dto.getBookingId());
        Booking booking = bookingRepo.findById(dto.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        authUser.assertCanActAs(booking.getUser().getUserId());

        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new InvalidBookingException("Booking is already cancelled");
        if (booking.getStatus() == BookingStatus.PENDING)
            return cancelPending(booking);
        if (booking.getStatus() == BookingStatus.CONFIRMED)
            return cancelConfirmed(booking);

        throw new InvalidBookingException("Invalid booking state");
    }

    private BookingCancelResponseDTO cancelPending(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
        invoiceRepo.findByBookingBookingId(booking.getBookingId()).stream()
            .filter(inv -> inv.getStatus() == PaymentStatus.PENDING)
            .forEach(inv -> { inv.setStatus(PaymentStatus.CANCELLED); invoiceRepo.save(inv); });
        helper.notify(booking.getUser(), "Booking cancelled (no payment made). Booking ID: " + booking.getBookingId());
        return buildCancelResponse(booking, 0.0, "NONE", LocalDateTime.now());
    }

    private BookingCancelResponseDTO cancelConfirmed(Booking booking) {
        double refund = helper.calculateRefundAmount(booking.getAmount(), booking.getBookingDate());
        String refundStatus = helper.resolveRefundStatus(refund, booking.getAmount());
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
        helper.createRefundInvoiceAndPayment(booking, refund);
        helper.notify(booking.getUser(), "Booking cancelled. Refund: " + refund + " | Booking ID: " + booking.getBookingId());
        return buildCancelResponse(booking, refund, refundStatus, LocalDateTime.now());
    }

    private BookingCancelResponseDTO buildCancelResponse(Booking booking, double refund,
            String refundStatus, LocalDateTime now) {
        return BookingCancelResponseDTO.builder()
            .bookingId(booking.getBookingId()).userId(booking.getUser().getUserId())
            .status(booking.getStatus()).originalAmount(booking.getAmount())
            .refundAmount(refund).deductionAmount(booking.getAmount() - refund)
            .bookingDate(booking.getBookingDate()).cancelledAt(now)
            .refundStatus(refundStatus).message("Booking cancelled successfully").build();
    }
}