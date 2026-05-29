package com.cts.serviceimpl;

import com.cts.dto.UserDTO;
import com.cts.dto.UserResponseDTO;
import com.cts.entity.User;
import com.cts.exception.UserNotFoundException;
import com.cts.repository.UserRepository;
import com.cts.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repo;
	private final PasswordEncoder encoder;

	@Override
	public UserResponseDTO register(UserDTO dto) {

		User user = User.builder().email(dto.getEmail()).password(encoder.encode(dto.getPassword())).role(dto.getRole())
				.phoneNo(dto.getPhoneNo()).status(dto.getStatus()).createdAt(LocalDateTime.now()) // ✅ FIXED
				.build();

		user = repo.save(user);
		return mapToResponseDTO(user);
	}

	@Override
	public UserResponseDTO login(String email, String password) {

		User user = repo.findByEmail(email);

		if (user == null || !encoder.matches(password, user.getPassword())) {
			throw new UserNotFoundException("Invalid login");
		}

		return mapToResponseDTO(user);
	}

	@Override
	public List<UserResponseDTO> getAllUsers() {
		return repo.findAll().stream().map(this::mapToResponseDTO).toList();
	}

	private UserResponseDTO mapToResponseDTO(User user) {
		return UserResponseDTO.builder().userId(user.getUserId()).email(user.getEmail()).role(user.getRole())
				.status(user.getStatus()).build();
	}
}
