package com.cts.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class PaymentDTO {

    @NotNull(message = "Invoice ID cannot be empty")
    private Long invoiceId;

    @NotNull(message = "Payment amount cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Payment method is required")
    
    @Pattern(regexp = "^(CREDIT_CARD|DEBIT_CARD|UPI|NET_BANKING|PAYPAL)$", 
             message = "Payment method must be one of: CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, PAYPAL")
    private String paymentMethod;
}