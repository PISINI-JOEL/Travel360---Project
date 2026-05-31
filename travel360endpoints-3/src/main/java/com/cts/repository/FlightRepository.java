package com.cts.repository;

import java.util.List;

import com.cts.entity.Flight;
import com.cts.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByPartner(Partner partner);

    Page<Flight> findBySourceAndDestination(String source, String destination, Pageable pageable);

    Page<Flight> findBySourceAndDestinationAndPriceBetween(
            String source,
            String destination,
            Double min,
            Double max,
            Pageable pageable
    );
}