package com.gymai.plan_service.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private int age;
    private double weight;
    private double height;
    private String fitnessGoal; // e.g., "weight_loss", "muscle_gain"

}
