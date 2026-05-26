package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.Flight;

import java.util.List;
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

	List<Flight> findBySourceAndDestination(String source, String destination);

	List<Flight> findBySourceAndDestinationAndPriceBetween(
	        String source,
	        String destination,
	        double min,
	        double max
	);
}