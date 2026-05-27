package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.PackageStatus;
import com.cts.enums.TravelPackageCategory;
import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class TravelPackageDTO {

    @NotBlank(message = "Package name is required")
    @Size(min = 3, max = 100, message = "Package name must be between 3 and 100 characters")
    private String packageName;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Package price must be greater than 0")
    private Double price;

    @NotNull(message = "Duration days cannot be empty")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    @NotNull(message = "Total slots cannot be empty")
    @Min(value = 1, message = "Total slots must be at least 1")
    private Integer totalSlots;

    private String description;

    @NotNull(message = "Travel package category is required")
    private TravelPackageCategory category;

    @NotNull(message = "Package status is required")
    private PackageStatus status;
}
