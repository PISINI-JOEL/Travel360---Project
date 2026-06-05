package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.InvoiceDTO;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@AllArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService service;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<InvoiceResponseDTO> create(@RequestBody @Valid InvoiceDTO dto) {

        log.info("Received request to create invoice for bookingId: {}", dto.getBookingId());
        auditLogService.logAction(AuditActions.CREATE_INVOICE, AuditEntity.INVOICE, null, authUser.currentOrNull(), LogType.INFO);

        InvoiceResponseDTO response = service.createInvoice(dto);

        log.info("Invoice created successfully with ID: {}", response.getInvoiceId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<List<InvoiceResponseDTO>> getAll() {

        log.info("Fetching all invoices");

        List<InvoiceResponseDTO> invoices = service.getAllInvoices();

        log.info("Total invoices fetched: {}", invoices.size());

        return new ResponseEntity<>(invoices, HttpStatus.OK);
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<List<InvoiceResponseDTO>> getByBooking(@PathVariable Long bookingId) {

        log.info("Fetching invoices for bookingId: {}", bookingId);

        List<InvoiceResponseDTO> invoices = service.getInvoicesByBooking(bookingId);

        log.info("Found {} invoices for bookingId: {}", invoices.size(), bookingId);

        return new ResponseEntity<>(invoices, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','FINANCE_OFFICER','ADMIN')")
    public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable Long id) {

        log.info("Fetching invoice with ID: {}", id);

        InvoiceResponseDTO invoice = service.getInvoiceById(id);

        log.info("Invoice fetched successfully with ID: {}", id);

        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }
}