package com.cts.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageItinerary {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long packageItineraryId;
	private LocalDate startDate;
	private LocalDate endDate;
	private String status;
	private String notes;
	private LocalDateTime createdAt;
	private String detailedDescription;
	private String keyHighlights;
	private String guideName;
	private String supportContact;
	

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "package_id" ,nullable = false)
    @JsonManagedReference
    private TravelPackage travelPackage;

	
}
