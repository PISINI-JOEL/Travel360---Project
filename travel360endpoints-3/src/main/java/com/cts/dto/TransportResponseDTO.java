package com.cts.dto;

import java.time.LocalTime;

import com.cts.enums.TransportStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransportResponseDTO {

    private Long transportId;
    private int transportNumber;
    private String source;
    private String destination;
    private String transportType;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    
    private int transportTotalSeats;
    private double price;
    private TransportStatus transportStatus;
}