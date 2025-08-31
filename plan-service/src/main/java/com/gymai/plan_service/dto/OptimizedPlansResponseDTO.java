
// OptimizedPlansResponseDTO.java
package com.gymai.plan_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedPlansResponseDTO {
  private UserProfileDTO user;
  private SimpleDietPlanDTO dietPlan;
  private SimpleWorkoutPlanDTO workoutPlan;
  private NutritionAnalysis nutritionAnalysis;
  private String summary;
  private boolean plansExist;
}