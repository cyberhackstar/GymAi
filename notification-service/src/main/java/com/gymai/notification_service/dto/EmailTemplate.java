package com.gymai.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {
    private String to;
    private String subject;
    private String body;
    private boolean isHtml;
}