package com.cts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBookingDTO {

	@NotNull(message = "Itinerary ID cannot be empty")
	private Long itineraryId;

	@NotNull(message = "Booking ID cannot be empty")
	private Long bookingId;
}
