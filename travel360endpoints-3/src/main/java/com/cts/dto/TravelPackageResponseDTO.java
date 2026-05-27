package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.PackageStatus;
import com.cts.enums.TravelPackageCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelPackageResponseDTO {

    private Long packageId;
    private String packageName;
    private String source;
    private String destination;
    private double price;
    private int durationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalSlots;
    private String description;
    private TravelPackageCategory category;
    private PackageStatus status;
}
