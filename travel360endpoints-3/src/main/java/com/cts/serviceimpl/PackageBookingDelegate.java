package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.PackageStatus;
import com.cts.exception.*;
import com.cts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PackageBookingDelegate {

    private final TravelPackageRepository packageRepo;
    private final BookingRepository bookingRepo;
    private final BookingHelper helper;

    @Transactional
    public BookingPackageResponseDTO createPackageBooking(BookingPackageDTO dto) {
        log.info("Creating package booking userId: {}, packageId: {}", dto.getUserId(), dto.getPackageId());
        User user = helper.fetchUser(dto.getUserId());
        TravelPackage tpackage = resolveAndValidate(dto);
        Booking booking = helper.buildPackageBooking(user, tpackage, dto);
        bookingRepo.save(booking);
        helper.createPendingInvoice(booking);
        helper.notify(user, "Package booked successfully. Booking ID: " + booking.getBookingId());
        return helper.toPackageResponse(booking, tpackage, user);
    }

    private TravelPackage resolveAndValidate(BookingPackageDTO dto) {
        TravelPackage tpackage = packageRepo.findById(dto.getPackageId())
            .orElseThrow(() -> new PackageNotFoundException("Package not found"));
        if (tpackage.getStatus() != PackageStatus.AVAILABLE)
            throw new InvalidBookingException("Package is not available for booking");
        if (tpackage.getTotalSlots() - bookingRepo.getBookedSlots(tpackage.getPackageId()) < dto.getUnits())
            throw new InsufficientAvailabilityException("Not enough package slots available");
        return tpackage;
    }
}