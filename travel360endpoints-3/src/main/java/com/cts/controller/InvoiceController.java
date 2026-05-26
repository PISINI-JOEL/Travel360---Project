package com.cts.controller;

import com.cts.dto.InvoiceDTO;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.service.InvoiceService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoices")
@AllArgsConstructor
public class InvoiceController {

	private final InvoiceService service;

	@PostMapping
	public ResponseEntity<InvoiceResponseDTO> create(@RequestBody @Valid InvoiceDTO dto) {

		return new ResponseEntity<>(service.createInvoice(dto), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<List<InvoiceResponseDTO>> getAll() {

		return new ResponseEntity<>(service.getAllInvoices(), HttpStatus.OK);
	}

	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<List<InvoiceResponseDTO>> getByBooking(@PathVariable Long bookingId) {

	    return new ResponseEntity<>(
	            service.getInvoicesByBooking(bookingId),
	            HttpStatus.OK
	    );
	}

	@GetMapping("/{id}")
	public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable Long id) {

		return new ResponseEntity<>(service.getInvoiceById(id), HttpStatus.OK);
	}
}
