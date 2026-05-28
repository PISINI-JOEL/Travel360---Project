package com.cts.dto;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageItineraryRequestDTO {
    private LocalDate start_date;
    private LocalDate end_date;
    private String status;
    private String notes;
    private String detailedDescription;
	private String keyHighlights;
	private String guideName;
	private String supportContact;

    // Reference to existing package
    private Long packageId;
}
