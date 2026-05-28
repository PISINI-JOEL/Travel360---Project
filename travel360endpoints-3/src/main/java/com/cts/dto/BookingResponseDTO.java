package com.cts.dto;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDTO {

    private Long bookingId;
    private BookingType bookingType;
    private double amount;
    private BookingStatus status;

    private Long userId;
    private String email;
    private int units;

   
    private Long flightId;
    private String flightNumber;

   
    private Long hotelId;
    private String hotelName;

   
    private Long transportId;
    private String transportType;


    private Long packageId;
    private String packageName;


    private Long itineraryId;
}
