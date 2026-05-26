package com.cts.dto;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPackageResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private double amount;
    private BookingStatus status;

    private Long userId;
    private String email;
    private int units;

    private String bookingName;
    private Gender gender;

    private Long packageId;
    private String packageName;
    private String destination;
}
