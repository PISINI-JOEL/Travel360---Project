package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.Gender;
import com.cts.enums.PackageStatus;
import com.cts.enums.TravelPackageCategory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingPackageResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private double amount;
    private BookingStatus status;
    private LocalDate bookingDate;

    private Long userId;
    private String email;
    private int units;

    private String bookingName;
    private Gender gender;

    private Long packageId;
    private String packageName;
    private String source;
    private String destination;
    private int durationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private TravelPackageCategory category;
    private PackageStatus packageStatus;
}
