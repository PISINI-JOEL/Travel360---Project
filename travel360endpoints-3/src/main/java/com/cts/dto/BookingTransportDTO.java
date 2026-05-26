package com.cts.dto;

import com.cts.enums.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookingTransportDTO {

    @NotNull(message = "User ID cannot be empty")
    private Long userId;        

    @NotNull(message = "Transport ID cannot be empty")
    private Long transportId;    

    @NotNull(message = "Number of transport seats/tickets cannot be empty")
    @Min(value = 1, message = "You must book at least 1 seat")
    @Max(value = 15, message = "You cannot book more than 15 seats at once")
    private Integer units;           

    @NotBlank(message = "Passenger name is required")
    @Size(min = 2, max = 70, message = "Passenger name must be between 2 and 70 characters")
    private String bookingName;  

    @NotNull(message = "Gender is required")
    private Gender gender;       
}