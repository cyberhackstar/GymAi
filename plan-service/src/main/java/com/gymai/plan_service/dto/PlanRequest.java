package com.gymai.plan_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRequest {
    private Long userId;
    private String name;
    private String email; // Optional, for logging/tracking
    private int age;
    private double weight;
    private double height;
    private String gender;
    private String goal; // e.g., "Lose Weight"
    private String activityLevel; // e.g., "Moderate"
    private String preference; // e.g., "Vegetarian"
}
