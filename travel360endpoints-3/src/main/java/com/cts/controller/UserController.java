package com.cts.controller;

import com.cts.dto.*;
import com.cts.entity.User;
import com.cts.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Tag(name = "User Controller", description = "Operations related to User Registration Login and getAll")

public class UserController {

	private final UserService service;
	
	@Operation(summary = "Register a new user")
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid UserDTO dto) {

		return new ResponseEntity<>(service.register(dto), HttpStatus.CREATED);
	}
	@Operation(summary = "Login for User")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {

		return new ResponseEntity<>(service.login(dto.getEmail(), dto.getPassword()), HttpStatus.OK);
	}
	@Operation(summary = "Get all Users")
	@GetMapping
	public ResponseEntity<List<User>> getAll() {

		return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
	}
}
