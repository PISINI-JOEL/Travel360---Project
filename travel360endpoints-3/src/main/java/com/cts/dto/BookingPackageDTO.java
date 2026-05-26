package com.cts.dto;

import com.cts.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookingPackageDTO {

    @NotNull(message = "User ID cannot be empty")
    private Long userId;

    @NotNull(message = "Package ID cannot be empty")
    private Long packageId;

    @NotNull(message = "Number of packages (units) cannot be empty")
    @Min(value = 1, message = "You must book at least 1 holiday package")
    @Max(value = 20, message = "You cannot book more than 20 packages at once")
    private Integer units; 

    @NotBlank(message = "Primary traveler name is required")
    @Size(min = 2, max = 100, message = "Traveler name must be between 2 and 100 characters")
    private String bookingName;

    @NotNull(message = "Gender is required")
    private Gender gender;
}