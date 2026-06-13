package com.cts.repository;

import com.cts.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserUserId(Long userId);

    List<Booking> findByItineraryItineraryId(Long itineraryId);

    @Query("SELECT COALESCE(SUM(b.units), 0) FROM Booking b WHERE b.flight.flightId = :flightId AND b.bookingDate = :bookingDate AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
    int getBookedSeats(@Param("flightId") long flightId, @Param("bookingDate") LocalDate bookingDate);

    @Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
           "WHERE b.hotel.hotelId = :hotelId AND b.status <> com.cts.enums.BookingStatus.CANCELLED " +
           "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    int getBookedRooms(@Param("hotelId") Long hotelId, @Param("checkInDate") LocalDate checkInDate, @Param("checkOutDate") LocalDate checkOutDate);

    @Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
           "WHERE b.travelPackage.packageId = :packageId AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
    int getBookedSlots(@Param("packageId") Long packageId);

    @Query("SELECT COALESCE(SUM(b.units),0) FROM Booking b " +
           "WHERE b.transport.transportId = :transportId AND b.bookingDate = :bookingDate " +
           "AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
    int getBookedTransportSeats(@Param("transportId") Long transportId, @Param("bookingDate") LocalDate bookingDate);

    // KPI Report Queries
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt <= :endDate AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
    java.math.BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt <= :endDate")
    long countBookingsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = com.cts.enums.BookingStatus.CANCELLED AND b.createdAt >= :startDate AND b.createdAt <= :endDate")
    long countCancellationsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.bookingType = :type AND b.createdAt >= :startDate AND b.createdAt <= :endDate AND b.status <> com.cts.enums.BookingStatus.CANCELLED")
    java.math.BigDecimal getRevenueByType(@Param("type") com.cts.enums.BookingType type, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.status = com.cts.enums.BookingStatus.CANCELLED AND b.createdAt >= :startDate AND b.createdAt <= :endDate")
    java.math.BigDecimal getCancelledRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.cts.dto.MonthlyKpiStatsDTO(" +
           "MONTH(b.createdAt), " +
           "SUM(CASE WHEN b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "COUNT(b), " +
           "SUM(CASE WHEN b.status = com.cts.enums.BookingStatus.CANCELLED THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.FLIGHT AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.HOTEL AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.TRANSPORT AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.PACKAGE AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.status = com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END)) " +
           "FROM Booking b WHERE YEAR(b.createdAt) = :year GROUP BY MONTH(b.createdAt) ORDER BY MONTH(b.createdAt)")
    List<com.cts.dto.MonthlyKpiStatsDTO> getMonthlyStats(@Param("year") int year);

    @Query("SELECT new com.cts.dto.KpiStatsDTO(" +
           "SUM(CASE WHEN b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "COUNT(b), " +
           "SUM(CASE WHEN b.status = com.cts.enums.BookingStatus.CANCELLED THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.FLIGHT AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.HOTEL AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.TRANSPORT AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.bookingType = com.cts.enums.BookingType.PACKAGE AND b.status <> com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END), " +
           "SUM(CASE WHEN b.status = com.cts.enums.BookingStatus.CANCELLED THEN b.amount ELSE 0 END)) " +
           "FROM Booking b WHERE b.createdAt >= :startDate AND b.createdAt <= :endDate")
    com.cts.dto.KpiStatsDTO getStats(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
