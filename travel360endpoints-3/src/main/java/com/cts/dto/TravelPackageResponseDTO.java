package com.cts.dto;

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
    private String destination;
    private double price;
    private int durationDays;
    private TravelPackageCategory category;
}
