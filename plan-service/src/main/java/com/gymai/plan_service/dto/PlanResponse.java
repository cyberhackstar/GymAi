package com.gymai.plan_service.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanResponse {
    private Long userId;
    private String goal;
    private List<String> recommendedDiet;
    private List<String> recommendedWorkout;
    private Integer caloriesTarget;
}
