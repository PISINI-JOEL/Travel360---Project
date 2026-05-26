package com.cts.entity;




import com.cts.enums.TransportStatus;




import java.time.LocalDateTime;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transport")
@Entity
public class Transport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transportId;
	
	private int transportNumber;
	private String source;
	private String destination;
	private String transportType;
;
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;
	private int transportAvailableSeats;
	private int transportTotalSeats;
	private double price;
	@Enumerated(EnumType.STRING)
	private TransportStatus transportStatus;
	
		
	
	
}
