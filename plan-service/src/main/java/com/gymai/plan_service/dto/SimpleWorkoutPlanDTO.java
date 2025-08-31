
// SimpleWorkoutPlanDTO.java
package com.gymai.plan_service.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWorkoutPlanDTO {
  private Long id;
  private Long userId;
  private String planType;
  private String difficultyLevel;
  private LocalDate createdDate;
  private List<SimpleDayWorkoutPlanDTO> weeklyPlan;
}