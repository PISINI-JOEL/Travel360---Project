package com.cts.dto;

import com.cts.enums.PaymentStatus;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponseDTO {

    private Long paymentId;
    private double amount;
    private PaymentStatus status;
}
