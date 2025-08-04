package com.gymai.auth_service.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * DTO for user registration.
 * Accepts role as a string to match entity.
 */
@Data
public class UserRegisterDto {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;
}
