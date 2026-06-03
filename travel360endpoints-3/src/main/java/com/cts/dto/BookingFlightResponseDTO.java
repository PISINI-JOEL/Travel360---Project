package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingFlightResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private double amount;
    private BookingStatus status;

    private Long userId;
    private String email;
    private int units;

    private String bookingName;
    private Gender gender;
    
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    
    private LocalDateTime createdAt;
    
    private LocalDate travelDate;


    private LocalDate bookingDate;

    private Long flightId;
    private String flightNumber;
    private String source;
    private String destination;

    private List<PassengerResponseDTO> passengers;
}