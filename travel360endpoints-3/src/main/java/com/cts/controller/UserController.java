package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.*;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import com.cts.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "User Controller", description = "Operations related to User Registration Login and getAll")
@Slf4j
public class UserController {

	private final UserService service;
	private final AuthenticatedUserProvider authUser;
	private final AuditLogService auditLogService;

	@Operation(summary = "Register a new user")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid UserDTO dto) {

		log.info("Received request to register user with email: {}", dto.getEmail());
		auditLogService.logAction(AuditActions.REGISTER_USER, AuditEntity.USER, null, authUser.currentOrNull(), LogType.INFO);

		UserResponseDTO registeredUser = service.register(dto);

		log.info("User registered successfully with ID: {}", registeredUser.getUserId());

		return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
	}
	@Operation(summary = "Login for User")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {

		log.info("Login attempt for email: {}", dto.getEmail());
		auditLogService.logAction(AuditActions.LOGIN_USER, AuditEntity.USER, null, authUser.currentOrNull(), LogType.INFO);

		AuthResponseDTO authResponse = service.login(dto.getEmail(), dto.getPassword());

		log.info("User logged in successfully with ID: {}", authResponse.getUser().getUserId());

		return new ResponseEntity<>(authResponse, HttpStatus.OK);
	}
	@Operation(summary = "Get all Users")
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserResponseDTO>> getAll() {

		log.info("Fetching all users");

		List<UserResponseDTO> users = service.getAllUsers();

		log.info("Total users fetched: {}", users.size());

		return new ResponseEntity<>(users, HttpStatus.OK);
	}
}
