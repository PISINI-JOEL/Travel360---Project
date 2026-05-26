package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookingHotelDTO {

    @NotNull(message = "User ID cannot be empty")
    private Long userId;

    @NotNull(message = "Hotel ID cannot be empty")
    private Long hotelId;

    @NotNull(message = "Number of rooms (units) cannot be empty")
    @Min(value = 1, message = "You must book at least 1 room")
    @Max(value = 5, message = "You cannot book more than 5 rooms at once")
    private Integer units;

    @NotBlank(message = "Guest name is required")
    @Size(min = 2, max = 100, message = "Guest name must be between 2 and 100 characters")
    private String bookingName;

    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotNull(message = "Number of days cannot be empty")
    @Min(value = 1, message = "Stay must be at least 1 day")
    @Max(value = 30, message = "Stay cannot exceed 30 days")
    private Integer days;
    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;
    
    
}