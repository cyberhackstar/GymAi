package com.gymai.plan_service.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDietPlanDTO {
  private Long id;
  private Long userId;
  private double dailyCalorieTarget;
  private double dailyProteinTarget;
  private double dailyCarbsTarget;
  private double dailyFatTarget;
  private LocalDate createdDate;
  private List<SimpleDayMealPlanDTO> dailyPlans;
}