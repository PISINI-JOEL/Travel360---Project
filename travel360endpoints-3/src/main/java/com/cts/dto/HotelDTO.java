package com.cts.dto;

import com.cts.enums.HotelStatus;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class HotelDTO {
    
    @NotBlank(message = "Hotel name is required")
    private String hotelName;

    @NotNull(message = "Ratings cannot be empty")
    @Min(value = 1, message = "Ratings must be at least 1 star")
    @Max(value = 5, message = "Ratings cannot exceed 5 stars")
    private Integer ratings; 

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price per night must be greater than 0")
    private Double price; 

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be exactly 10 digits")
    private String contactNo;

    @NotBlank(message = "Email ID is required")
    @Email(message = "Invalid email format")
    private String emailId;
    
    @NotNull(message = "Total rooms cannot be empty")
    @Min(value = 1, message = "At least 1 room required")
    private Integer totalRooms;
    

    @NotNull(message = "Hotel status is required")
    private HotelStatus status;
}