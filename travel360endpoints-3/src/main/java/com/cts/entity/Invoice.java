package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.cts.enums.PaymentStatus;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    private LocalDateTime invoiceDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    


}