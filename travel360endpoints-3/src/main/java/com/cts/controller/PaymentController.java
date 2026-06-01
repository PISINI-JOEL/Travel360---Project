package com.cts.controller;

import com.cts.dto.PaymentDTO;
import com.cts.dto.PaymentResponseDTO;
import com.cts.service.PaymentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<?> makePayment(@RequestBody @Valid PaymentDTO dto) {

        log.info("Received payment request for invoiceId: {}", dto.getInvoiceId());

        PaymentResponseDTO payment = service.makePayment(dto);

        log.info("Payment processed successfully. paymentId={}, amount={}",
                payment.getPaymentId(), payment.getAmount());

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<?> getByInvoice(@PathVariable Long invoiceId) {

        log.info("Fetching payments for invoiceId: {}", invoiceId);

        List<PaymentResponseDTO> payments = service.getPaymentsByInvoice(invoiceId);

        if (payments.isEmpty()) {
            log.warn("No payments found for invoiceId: {}", invoiceId);
            return ResponseEntity.ok("No payments found");
        }

        log.info("Found {} payments for invoiceId: {}", payments.size(), invoiceId);

        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {

        log.info("Fetching all payments");

        List<PaymentResponseDTO> payments = service.getAllPayments();

        log.info("Total payments fetched: {}", payments.size());

        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        log.info("Fetching payment with ID: {}", id);

        PaymentResponseDTO payment = service.getPaymentById(id);

        log.info("Payment fetched successfully with ID: {}", id);

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
