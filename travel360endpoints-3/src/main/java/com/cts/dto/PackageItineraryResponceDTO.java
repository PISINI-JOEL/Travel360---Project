package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.PackageStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageItineraryResponceDTO {

    // Package details
    private Long packageId;
    private String packageName;
    private String description;
    private int durationDays;
    private double price;
    private  PackageStatus status;
    private String destination;

    // Itinerary details
    private Long packageItineraryId;
    private LocalDate start_date;
    private LocalDate end_date;
    private String itineraryStatus;
    private String notes;
    private LocalDateTime created_at;
    private String detailedDescription;
	private String keyHighlights;
	private String guideName;
	private String supportContact;
}
