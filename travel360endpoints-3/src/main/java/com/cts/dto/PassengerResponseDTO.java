package com.cts.dto;

import java.time.LocalDate;

import com.cts.enums.Gender;
import com.cts.enums.PassengerStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerResponseDTO {

	private Long passengerId;
	private String passengerName;
	private LocalDate dateOfBirth;
	private Gender gender;
	private String contactNo;
	private String emailAddress;
	private String nationality;
	private String identificationNumber;
	private PassengerStatus status;
}
