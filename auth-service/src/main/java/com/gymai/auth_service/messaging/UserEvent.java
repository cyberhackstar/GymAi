package com.gymai.auth_service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
  private String email;
  private String username;
  private String eventType; // e.g., "REGISTRATION" or "LOGIN"
}