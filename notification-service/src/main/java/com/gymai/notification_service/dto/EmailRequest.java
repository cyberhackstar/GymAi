
package com.gym.notification.dto;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String token;
}
