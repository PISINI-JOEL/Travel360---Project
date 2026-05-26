package com.cts.dto;

import com.cts.enums.BookingType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingDTO {
@NotNull(message="userId cannot be empty")
    private Long userId;
@NotNull(message="flightId cannot be empty")
    private Long flightId;
@NotNull(message="BookingType is required")
    private BookingType bookingType;
@NotNull(message="Amount cannot be empty")
@DecimalMin(value="500",message="amount must be greater than 500")

    private Double amount;
    
    @NotNull(message="Units cannot be empty")
    @Min(value=1,message = "units cannot be less than one")
    @Max(value=10,message="units cannot exceed ten")
    
        private Integer units;   
    
}

