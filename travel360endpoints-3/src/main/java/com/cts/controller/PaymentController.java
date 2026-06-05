package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.PaymentDTO;
import com.cts.dto.PaymentResponseDTO;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.PaymentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService service;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','CORPORATE_TRAVEL_MANAGER')")
    public ResponseEntity<?> makePayment(@RequestBody @Valid PaymentDTO dto) {

        log.info("Received payment request for invoiceId: {}", dto.getInvoiceId());
        auditLogService.logAction(AuditActions.MAKE_PAYMENT, AuditEntity.PAYMENT, null, authUser.currentOrNull(), LogType.INFO);

        PaymentResponseDTO payment = service.makePayment(dto);

        log.info("Payment processed successfully. paymentId={}, amount={}",
                payment.getPaymentId(), payment.getAmount());

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','FINANCE_OFFICER','ADMIN')")
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
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {

        log.info("Fetching all payments");

        List<PaymentResponseDTO> payments = service.getAllPayments();

        log.info("Total payments fetched: {}", payments.size());

        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        log.info("Fetching payment with ID: {}", id);

        PaymentResponseDTO payment = service.getPaymentById(id);

        log.info("Payment fetched successfully with ID: {}", id);

        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
