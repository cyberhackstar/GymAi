
// SimpleFoodItemDTO.java
package com.gymai.plan_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleFoodItemDTO {
  private String foodName;
  private String category;
  private double quantity;
  private double calories;
  private double protein;
  private double carbs;
  private double fat;
  private double fiber;
}