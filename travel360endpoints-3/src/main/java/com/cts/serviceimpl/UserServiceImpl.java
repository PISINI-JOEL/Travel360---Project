package com.cts.serviceimpl;

import com.cts.config.JWTUtil;
import com.cts.constants.AuditActions;
import com.cts.dto.AuthResponseDTO;
import com.cts.dto.UserDTO;
import com.cts.dto.UserResponseDTO;
import com.cts.entity.User;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.exception.UserNotFoundException;
import com.cts.repository.UserRepository;
import com.cts.service.AuditLogService;
import com.cts.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository repo;
	private final PasswordEncoder encoder;
	private final JWTUtil jwtUtil;
	private final AuditLogService auditLogService;

	@Override
	public UserResponseDTO register(UserDTO dto) {

		log.info("Registering new user with email: {}", dto.getEmail());

		User user = User.builder().email(dto.getEmail()).password(encoder.encode(dto.getPassword())).role(dto.getRole())
				.phoneNo(dto.getPhoneNo()).status(dto.getStatus()).createdAt(LocalDateTime.now()) // ✅ FIXED
				.build();

		user = repo.save(user);
		auditLogService.logAction(AuditActions.REGISTER_USER, AuditEntity.USER, user.getUserId(), user, LogType.INFO);

		log.info("User registered successfully with ID: {}", user.getUserId());

		return mapToResponseDTO(user);
	}

	@Override
	public AuthResponseDTO login(String email, String password) {

		log.info("Login attempt for email: {}", email);

		User user = repo.findByEmail(email);

		if (user == null || !encoder.matches(password, user.getPassword())) {
			log.error("Invalid login attempt for email: {}", email);
			throw new UserNotFoundException("Invalid login");
		}

		String token = jwtUtil.generateToken(user.getEmail(),user.getRole());
		auditLogService.logAction(AuditActions.LOGIN_USER, AuditEntity.USER, user.getUserId(), user, LogType.INFO);

		log.info("User logged in successfully with ID: {}", user.getUserId());

		return AuthResponseDTO.builder()
				.token(token)
				.user(mapToResponseDTO(user))
				.build();
	}

	@Override
	public List<UserResponseDTO> getAllUsers() {

		log.info("Fetching all users");

		List<UserResponseDTO> users = repo.findAll().stream().map(this::mapToResponseDTO).toList();

		log.info("Total users fetched: {}", users.size());

		return users;
	}

	private UserResponseDTO mapToResponseDTO(User user) {
		return UserResponseDTO.builder().userId(user.getUserId()).email(user.getEmail()).role(user.getRole())
				.status(user.getStatus()).build();
	}
}
