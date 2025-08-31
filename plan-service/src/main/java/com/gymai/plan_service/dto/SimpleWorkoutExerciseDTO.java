
// SimpleWorkoutExerciseDTO.java
package com.gymai.plan_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleWorkoutExerciseDTO {
  private String exerciseName;
  private String category;
  private String muscleGroup;
  private String equipment;
  private String difficulty;
  private String description;
  private String instructions;
  private int sets;
  private int reps;
  private int durationMinutes;
  private double weight;
  private int restSeconds;
  private double caloriesBurned;
  private double caloriesBurnedPerMinute;
}