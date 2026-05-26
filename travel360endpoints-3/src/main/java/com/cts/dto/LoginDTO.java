package com.cts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
	@NotBlank(message = "Email is mandatory")
	@Email(message="Invalid email format")
    private String email;
	@NotBlank(message="password is mandatory")
	@Size(min=8,max =25,message="password must have a min length of 8 and maxlength of 25")
	
    private String password;
}
