package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.TransportDTO;
import com.cts.dto.TransportResponseDTO;
import com.cts.enums.TransportStatus;
import com.cts.service.TransportService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/transports")
@AllArgsConstructor
@Validated
public class TransportController {

	private final TransportService service;

	@PostMapping
	public ResponseEntity<?> addTransport(@RequestBody @Valid TransportDTO dto) {

		return new ResponseEntity<>(service.addTransport(dto), HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateTransport(@PathVariable Long id, @RequestBody @Valid TransportDTO dto) {

		return new ResponseEntity<>(service.updateTransport(id, dto), HttpStatus.OK);
	}

	@GetMapping
	public List<TransportResponseDTO> getAll(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {
		return service.getAllTransports(page,size);
	}

	@GetMapping("/search")
	public List<TransportResponseDTO> findByRoute(@RequestParam String source, @RequestParam String destination,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

		return service.findByRoute(source, destination,page,size);
	}

	@GetMapping("/status/{status}")
	public List<TransportResponseDTO> findByStatus(@PathVariable TransportStatus status,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {

		return service.findByStatus(status,page,size);
	}
}
