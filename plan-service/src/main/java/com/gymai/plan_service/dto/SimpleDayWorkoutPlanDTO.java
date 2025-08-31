// SimpleDayWorkoutPlanDTO.java
package com.gymai.plan_service.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDayWorkoutPlanDTO {
  private int dayNumber;
  private String dayName;
  private String focusArea;
  private boolean restDay;
  private int estimatedDurationMinutes;
  private double totalCaloriesBurned;
  private List<SimpleWorkoutExerciseDTO> exercises;
}