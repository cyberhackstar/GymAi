// WorkoutPlanMapper.java
package com.gymai.plan_service.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.gymai.plan_service.dto.*;
import com.gymai.plan_service.entity.*;

@Component
public class WorkoutPlanMapper {

  public SimpleWorkoutPlanDTO toDTO(WorkoutPlan workoutPlan) {
    if (workoutPlan == null)
      return null;

    SimpleWorkoutPlanDTO dto = new SimpleWorkoutPlanDTO();
    dto.setId(workoutPlan.getId());
    dto.setUserId(workoutPlan.getUserId());
    dto.setPlanType(workoutPlan.getPlanType());
    dto.setDifficultyLevel(workoutPlan.getDifficultyLevel());
    dto.setCreatedDate(workoutPlan.getCreatedDate());

    List<SimpleDayWorkoutPlanDTO> weeklyPlanDTO = workoutPlan.getWeeklyPlan().stream()
        .map(this::toDayWorkoutPlanDTO)
        .collect(Collectors.toList());
    dto.setWeeklyPlan(weeklyPlanDTO);

    return dto;
  }

  private SimpleDayWorkoutPlanDTO toDayWorkoutPlanDTO(DayWorkoutPlan dayPlan) {
    SimpleDayWorkoutPlanDTO dto = new SimpleDayWorkoutPlanDTO();
    dto.setDayNumber(dayPlan.getDayNumber());
    dto.setDayName(dayPlan.getDayName());
    dto.setFocusArea(dayPlan.getFocusArea());
    dto.setRestDay(dayPlan.isRestDay());
    dto.setEstimatedDurationMinutes(dayPlan.getEstimatedDurationMinutes());
    dto.setTotalCaloriesBurned(dayPlan.getTotalCaloriesBurned());

    List<SimpleWorkoutExerciseDTO> exercisesDTO = dayPlan.getExercises().stream()
        .map(this::toWorkoutExerciseDTO)
        .collect(Collectors.toList());
    dto.setExercises(exercisesDTO);

    return dto;
  }

  private SimpleWorkoutExerciseDTO toWorkoutExerciseDTO(WorkoutExercise workoutExercise) {
    SimpleWorkoutExerciseDTO dto = new SimpleWorkoutExerciseDTO();
    dto.setExerciseName(workoutExercise.getExercise().getName());
    dto.setCategory(workoutExercise.getExercise().getCategory());
    dto.setMuscleGroup(workoutExercise.getExercise().getMuscleGroup());
    dto.setEquipment(workoutExercise.getExercise().getEquipment());
    dto.setDifficulty(workoutExercise.getExercise().getDifficulty());
    dto.setDescription(workoutExercise.getExercise().getDescription());
    dto.setInstructions(workoutExercise.getExercise().getInstructions());
    dto.setSets(workoutExercise.getSets());
    dto.setReps(workoutExercise.getReps());
    dto.setDurationMinutes(workoutExercise.getDurationMinutes());
    dto.setWeight(workoutExercise.getWeight());
    dto.setRestSeconds(workoutExercise.getRestSeconds());
    dto.setCaloriesBurned(workoutExercise.getCaloriesBurned());
    dto.setCaloriesBurnedPerMinute(workoutExercise.getExercise().getCaloriesBurnedPerMinute());
    return dto;
  }
}