package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.cts.enums.FlightStatus;

@Entity
@Table(name = "flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long flightId;

	private String flightNumber;
	private String airlineName;

	private String source;
	private String destination;
	
	private LocalTime arrivalTime;
	private LocalTime departureTime;

	
	private LocalDate flightDate;
	private int totalSeats;
	//private int availableSeats;
	private double price;
	@Enumerated(EnumType.STRING)
	private FlightStatus status;
}
