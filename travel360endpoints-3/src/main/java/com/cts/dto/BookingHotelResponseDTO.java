package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingHotelResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private double amount;
    private BookingStatus status;

    private Long userId;
    private String email;
    private int units;

    private String bookingName;
    private Gender gender;

    private Long hotelId;
    private String hotelName;
    private String city;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int days;

}

