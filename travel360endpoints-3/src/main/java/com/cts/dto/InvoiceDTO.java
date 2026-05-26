package com.cts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class InvoiceDTO {
@NotNull(message= "Booking ID cannot be null")
    private Long bookingId;  
    
}
