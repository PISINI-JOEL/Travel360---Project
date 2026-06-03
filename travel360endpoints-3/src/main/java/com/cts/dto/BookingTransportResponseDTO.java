package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingTransportResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private double amount;
    private BookingStatus status;
    private LocalDate bookingDate;
    private LocalDate travelDate;

    private Long userId;
    private String email;
    private int units;

    private String bookingName;
    private Gender gender;

    private Long transportId;
    private String source;
    private String destination;
    private String transportType;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    private List<PassengerResponseDTO> passengers;
}
