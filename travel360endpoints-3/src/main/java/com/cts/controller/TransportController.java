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
	public List<TransportResponseDTO> getAll(int page,int size) {
		return service.getAllTransports(page,size);
	}

	@GetMapping("/search")
	public List<TransportResponseDTO> findByRoute(@RequestParam String source, @RequestParam String destination,int page,int size) {

		return service.findByRoute(source, destination,page,size);
	}

	@GetMapping("/status/{status}")
	public List<TransportResponseDTO> findByStatus(@PathVariable TransportStatus status,int page,int size) {

		return service.findByStatus(status,page,size);
	}
}
