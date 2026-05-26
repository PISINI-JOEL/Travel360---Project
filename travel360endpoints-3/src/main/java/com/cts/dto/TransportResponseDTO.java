package com.cts.dto;

import java.time.LocalDateTime;

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
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int transportAvailableSeats;
    private int transportTotalSeats;
    private double price;
    private TransportStatus transportStatus;
}