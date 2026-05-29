package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.Gender;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class PassengerRequestDTO {

	@NotNull(message = "Booking ID is required")
	private Long bookingId;

	@NotBlank(message = "Full name is required")
	private String passengerName;

	@NotNull(message = "Date of birth is required")
	@Past(message = "DOB must be in the past")
	private LocalDate dateOfBirth;

	@NotNull(message = "Gender is required")
	private Gender gender;

	@NotBlank(message = "Contact Number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Invalid contact number")
	private String contactNo;

	@Email(message = "Invalid email")
	@NotBlank(message = "Email is required")
	private String emailAddress;

	@NotBlank(message = "Nationality is required")
	private String nationality;

	@NotBlank(message = "Identification number is required(Aadhar number required)")
	@Pattern(regexp = "^[0-9]{12}$", message = "Invalid Aadhar number")
	private String identificationNumber;
}