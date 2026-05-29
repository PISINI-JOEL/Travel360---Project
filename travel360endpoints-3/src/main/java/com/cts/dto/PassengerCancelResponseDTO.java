package com.cts.dto;

import java.time.LocalDateTime;

import com.cts.enums.BookingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerCancelResponseDTO {

	private Long bookingId;
	private Long passengerId;
	private String passengerName;

	private BookingStatus bookingStatus;
	private int remainingUnits;

	private Double refundAmount;
	private Double deductionAmount;
	private String refundStatus;

	private LocalDateTime cancelledAt;
	private String message;
}
