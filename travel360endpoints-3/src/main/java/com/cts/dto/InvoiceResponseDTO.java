package com.cts.dto;

import com.cts.enums.PaymentStatus;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceResponseDTO {

    private Long invoiceId;
    private double amount;
    private PaymentStatus status;

    private Long bookingId;  

    private Long userId;
    private String email;
}