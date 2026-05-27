package com.cts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingCancelDTO {
	@NotNull(message="userID cannot be empty")
	private long userId;
	@NotNull(message="BookingId cannot be empty")
	private long bookingId;
	

}
