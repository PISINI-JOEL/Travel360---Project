package com.cts.repository;

import com.cts.entity.PackageItinerary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageItineraryRepository extends JpaRepository<PackageItinerary, Long> {
	
	List<PackageItinerary> findByTravelPackage_PackageId(Long packageId);
}
