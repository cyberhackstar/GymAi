package com.gymai.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response.
 * Returns the JWT token upon successful login or registration.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
}
