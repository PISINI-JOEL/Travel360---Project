package com.cts.serviceimpl;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.HotelStatus;
import com.cts.exception.*;
import com.cts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelBookingDelegate {

    private final HotelRepository hotelRepo;
    private final BookingRepository bookingRepo;
    private final BookingHelper helper;

    @Transactional
    public BookingHotelResponseDTO createHotelBooking(BookingHotelDTO dto) {
        log.info("Creating hotel booking userId: {}, hotelId: {}", dto.getUserId(), dto.getHotelId());
        User user = helper.fetchUser(dto.getUserId());
        Hotel hotel = resolveAndValidate(dto);
        long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        Booking booking = helper.buildHotelBooking(user, hotel, dto, days);
        bookingRepo.save(booking);
        helper.createPendingInvoice(booking);
        helper.notify(user, "Hotel booked successfully. Booking ID: " + booking.getBookingId());
        return helper.toHotelResponse(booking, hotel, user);
    }

    private Hotel resolveAndValidate(BookingHotelDTO dto) {
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
            .orElseThrow(() -> new HotelNotFoundException("Hotel not found"));
        if (hotel.getStatus() != HotelStatus.AVAILABLE)
            throw new InvalidBookingException("Hotel is not available for booking");
        long days = ChronoUnit.DAYS.between(dto.getCheckInDate(), dto.getCheckOutDate());
        if (days <= 0)
            throw new InvalidBookingException("Check-out date must be after check-in date");
        if (hotel.getTotalRooms() - bookingRepo.getBookedRooms(hotel.getHotelId(), dto.getCheckInDate(), dto.getCheckOutDate()) < dto.getUnits())
            throw new InsufficientAvailabilityException("Not enough rooms available");
        return hotel;
    }
}