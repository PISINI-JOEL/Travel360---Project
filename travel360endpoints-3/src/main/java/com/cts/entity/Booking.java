package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.cts.enums.BookingStatus;
import com.cts.enums.BookingType;
import com.cts.enums.Gender;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
//check
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookingId;

	private String bookingName;

	@Enumerated(EnumType.STRING)
	private BookingType bookingType;

	private LocalDate bookingDate;
	
	
	

	private double amount;

	@Enumerated(EnumType.STRING)
	private BookingStatus status;

	@Enumerated(EnumType.STRING)
	private Gender gender;
	private int units;

	@ManyToOne
	private User user;

	@ManyToOne
	@JoinColumn(name="flight_id")
	private Flight flight;

	@ManyToOne
	@JoinColumn(name = "hotel_id")
	private Hotel hotel;

	@ManyToOne
	@JoinColumn(name = "package_id")
	private TravelPackage travelPackage;
	
	@ManyToOne
	@JoinColumn(name = "transport_id")
	private Transport transport;

	@ManyToOne
	@JoinColumn(name = "itinerary_id")
	private Itinerary itinerary;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Passenger> passengers = new ArrayList<>();

	private LocalDate checkInDate;
	private LocalDate checkOutDate;
	
	private LocalDateTime createdAt;
	private int days; 

}
