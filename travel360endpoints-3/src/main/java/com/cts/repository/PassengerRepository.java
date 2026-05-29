package com.cts.repository;

import com.cts.entity.Passenger;
import com.cts.enums.PassengerStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    long countByBookingBookingIdAndStatus(Long bookingId, PassengerStatus status);
}
