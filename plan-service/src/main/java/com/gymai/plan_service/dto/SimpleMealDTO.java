// SimpleMealDTO.java
package com.gymai.plan_service.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMealDTO {
  private String mealType;
  private double totalCalories;
  private double totalProtein;
  private double totalCarbs;
  private double totalFat;
  private List<SimpleFoodItemDTO> foodItems;
}