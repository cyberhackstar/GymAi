package com.gymai.auth_service.entity;


import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
