package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.BookingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingCancelResponseDTO {

    private Long bookingId;
    private Long userId;

    private BookingStatus status;

    
    private Double originalAmount;
    private Double refundAmount;
    private Double deductionAmount;

    
    private LocalDate bookingDate;
    private LocalDateTime cancelledAt;

    
    private String refundStatus;   
    private String message;
}