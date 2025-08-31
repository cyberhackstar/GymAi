// SimpleDayMealPlanDTO.java
package com.gymai.plan_service.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDayMealPlanDTO {
  private int dayNumber;
  private String dayName;
  private double totalDailyCalories;
  private double totalDailyProtein;
  private double totalDailyCarbs;
  private double totalDailyFat;
  private List<SimpleMealDTO> meals;
}