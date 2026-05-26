package com.cts.repository;

import com.cts.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserUserId(Long userId);

	@Query("SELECT COALESCE(SUM(b.units), 0) FROM Booking b WHERE b.flight.flightId =:flightId AND b.flight.flightDate = :bookingDate")
   public int getBookedSeats(@Param("flightId") long flightId,
                       @Param("bookingDate") LocalDate bookingDate);
	
	@Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
		       "WHERE b.hotel.hotelId = :hotelId")
		int getBookedRooms(@Param("hotelId") Long hotelId);
}
