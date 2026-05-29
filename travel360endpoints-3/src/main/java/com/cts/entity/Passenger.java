package com.cts.entity;

import java.time.LocalDate;

import com.cts.enums.Gender;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name="person")
public class Passenger {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long passengerId;
	private String passengerName;
	private LocalDate dateOfBirth;
	private Gender gender;
	private String contactNo;
	private String emailAddress;
	private String nationality;
	private String identificationNumber;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

}
