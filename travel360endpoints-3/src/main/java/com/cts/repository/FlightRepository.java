package com.cts.repository;

import com.cts.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    Page<Flight> findBySourceAndDestination(String source, String destination, Pageable pageable);

    Page<Flight> findBySourceAndDestinationAndPriceBetween(
            String source,
            String destination,
            Double min,
            Double max,
            Pageable pageable
    );
}