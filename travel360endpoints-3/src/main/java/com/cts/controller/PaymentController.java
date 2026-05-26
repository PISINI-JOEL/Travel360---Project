package com.cts.controller;

import com.cts.dto.PaymentDTO;
import com.cts.dto.PaymentResponseDTO;
import com.cts.service.PaymentService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService service;

    
    @PostMapping
    public ResponseEntity<?> makePayment(@RequestBody @Valid PaymentDTO dto) {

        
            PaymentResponseDTO payment = service.makePayment(dto);
            return new ResponseEntity<>(payment,HttpStatus.OK);
        
    }

    
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<?> getByInvoice(@PathVariable Long invoiceId) {

        List<PaymentResponseDTO> payments = service.getPaymentsByInvoice(invoiceId);

        if (payments.isEmpty()) {
            return ResponseEntity.ok("No payments found");
        }

        return new ResponseEntity(payments,HttpStatus.OK);
    }

    
    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments() {

        List<PaymentResponseDTO> payments = service.getAllPayments();
        return new ResponseEntity(payments,HttpStatus.OK);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {

        PaymentResponseDTO payment = service.getPaymentById(id);

        

        return new ResponseEntity(payment,HttpStatus.OK);
    }
}