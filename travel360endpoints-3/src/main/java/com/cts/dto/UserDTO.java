package com.cts.dto;

import com.cts.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 8, max = 25, message = "Password must be between 8 and 25 characters")
	private String password;

	 @NotNull(message = "Role is required") 
	private Role role;

	@NotNull(message = "Phone number cannot be empty")

	@Min(value = 1000000000L, message = "Phone number must be a valid 10-digit number")
	@Max(value = 9999999999L, message = "Phone number must be a valid 10-digit number")
	private Long phoneNo;

	@NotBlank(message = "Status is required")
	@Pattern(regexp = "^(ACTIVE|INACTIVE|BLOCKED)$", message = "Status must be one of: ACTIVE, INACTIVE, BLOCKED")
	private String status;
}