package com.gymai.plan_service.entity;

import java.util.ArrayList;
import java.util.List;

// DayWorkoutPlan.java
public class DayWorkoutPlan {
    private int dayNumber;
    private String dayName;
    private String focusArea; // UPPER_BODY, LOWER_BODY, FULL_BODY, CARDIO, REST
    private List<WorkoutExercise> exercises;
    private int estimatedDurationMinutes;
    private double totalCaloriesBurned;
    private boolean isRestDay;

    public DayWorkoutPlan(int dayNumber, String dayName, String focusArea) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.focusArea = focusArea;
        this.exercises = new ArrayList<>();
        this.isRestDay = "REST".equals(focusArea);
    }

    public void addExercise(WorkoutExercise exercise) {
        this.exercises.add(exercise);
        calculateTotals();
    }

    private void calculateTotals() {
        this.totalCaloriesBurned = exercises.stream()
                .mapToDouble(WorkoutExercise::getCaloriesBurned)
                .sum();

        this.estimatedDurationMinutes = exercises.stream()
                .mapToInt(ex -> {
                    if (ex.getDurationMinutes() > 0) {
                        return ex.getDurationMinutes();
                    } else {
                        // Estimate time for strength exercises
                        return (ex.getSets() * ex.getReps() * 3) + (ex.getSets() * ex.getRestSeconds());
                    }
                })
                .sum() / 60; // Convert to minutes
    }

    // Getters and setters
    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getFocusArea() {
        return focusArea;
    }

    public void setFocusArea(String focusArea) {
        this.focusArea = focusArea;
    }

    public List<WorkoutExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExercise> exercises) {
        this.exercises = exercises;
        calculateTotals();
    }

    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public boolean isRestDay() {
        return isRestDay;
    }

    public void setRestDay(boolean restDay) {
        isRestDay = restDay;
    }
}