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
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/transports")
@AllArgsConstructor
public class TransportController {

	private final TransportService service;

	@PostMapping
	public ResponseEntity<?> addTransport(@RequestBody @Valid TransportDTO dto) {

		return new ResponseEntity<>(service.addTransport(dto), HttpStatus.CREATED);
	}

	@GetMapping
	public List<TransportResponseDTO> getAll() {
		return service.getAllTransports();
	}

	@GetMapping("/search")
	public List<TransportResponseDTO> findByRoute(@RequestParam String source, @RequestParam String destination) {

		return service.findByRoute(source, destination);
	}

	@GetMapping("/status/{status}")
	public List<TransportResponseDTO> findByStatus(@PathVariable TransportStatus status) {

		return service.findByStatus(status);
	}
}
