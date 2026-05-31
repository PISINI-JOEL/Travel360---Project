package com.cts.dto;

import java.time.LocalDateTime;
import com.cts.enums.TransportStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TransportDTO {

    @NotNull(message = "Transport number is required")
    @Min(value = 100, message = "Transport number must be at least a 3-digit valid identifier")
    private Integer transportNumber; 

    @NotBlank(message = "Source location is required")
    private String source;

    @NotBlank(message = "Destination location is required")
    private String destination;

    @NotBlank(message = "Transport type is required")
    @Pattern(regexp = "^BUS$", message = "Transport type must be BUS")
    private String transportType;

    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be a future date and time")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    @Future(message = "Arrival time must be a future date and time")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Total seats capacity cannot be empty")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer transportTotalSeats; 

    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price; 

    @NotNull(message = "Transport status is required")
    private TransportStatus transportStatus;

    @NotNull(message = "Partner id is required")
    private Long partnerId;
}