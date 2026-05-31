package com.cts.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.cts.enums.FlightStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class FlightDTO {

    @NotBlank(message = "Flight number is required")
    @Pattern(regexp = "^[A-Z]{2}-\\d{3}$", message = "Flight number must match standard airline format (e.g., AA-123 or DL-1234)")
    private String flightNumber;

    @NotNull(message = "Partner (airline) id is required")
    private Long partnerId;

    @NotBlank(message = "Source location is required")
    private String source;

    @NotBlank(message = "Destination location is required")
    private String destination;

    private LocalTime arrivalTime;
    private LocalTime departureTime;
    
    
    private LocalDate flightDate;
    
    @NotNull(message = "Total seats cannot be empty")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;

    

    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price; 

    @NotNull(message = "Flight status is required")
    private FlightStatus status;
}