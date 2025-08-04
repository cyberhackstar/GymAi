package com.gymai.notification_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String email;
    private String subject;
    private String message;
}
