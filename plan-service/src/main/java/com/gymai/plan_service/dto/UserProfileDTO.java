// UserProfileDTO.java
package com.gymai.plan_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String name;
    private String email;
    private Integer age;
    private Double height;
    private Double weight;
    private String gender;
    private String goal;
    private String activityLevel;
    private String preference;
    private boolean profileComplete;
}