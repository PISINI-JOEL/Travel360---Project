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

    List<Booking> findByItineraryItineraryId(Long itineraryId);

	@Query("SELECT COALESCE(SUM(b.units), 0) FROM Booking b WHERE b.flight.flightId = :flightId AND b.bookingDate = :bookingDate AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
   public int getBookedSeats(@Param("flightId") long flightId,
                       @Param("bookingDate") LocalDate bookingDate);

	@Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
		       "WHERE b.hotel.hotelId = :hotelId AND b.status <> com.cts.enums.BookingStatus.CANCELLED " +
		       "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
		int getBookedRooms(@Param("hotelId") Long hotelId,
		                   @Param("checkInDate") LocalDate checkInDate,
		                   @Param("checkOutDate") LocalDate checkOutDate);

	@Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
		       "WHERE b.travelPackage.packageId = :packageId AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
		int getBookedSlots(@Param("packageId") Long packageId);

	@Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
		       "WHERE b.transport.transportId = :transportId AND b.bookingDate = :bookingDate " +
		       "AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
		int getBookedTransportSeats(@Param("transportId") Long transportId,
		                            @Param("bookingDate") LocalDate bookingDate);
}
