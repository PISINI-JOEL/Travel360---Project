package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItineraryResponseDTO {

	private Long itineraryId;
	private String tripName;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDateTime createdAt;

	private Long userId;
	private String email;

	private List<BookingResponseDTO> bookings;

	private double totalTripAmount;
}
