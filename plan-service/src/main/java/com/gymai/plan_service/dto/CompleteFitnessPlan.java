package com.gymai.plan_service.dto;

import java.time.LocalDate;

import com.gymai.plan_service.entity.DietPlan;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.entity.WorkoutPlan;

// Response DTOs
public class CompleteFitnessPlan {
  private User user;
  private DietPlan dietPlan;
  private WorkoutPlan workoutPlan;
  private LocalDate generatedDate;
  private String summary;

  // Constructor
  public CompleteFitnessPlan() {
    this.generatedDate = LocalDate.now();
    generateSummary();
  }

  private void generateSummary() {
    if (user != null && dietPlan != null && workoutPlan != null) {
      this.summary = String.format(
          "Complete fitness plan for %s - Goal: %s | Daily Calories: %.0f | Workout Days: %d/week",
          user.getName(),
          user.getGoal(),
          dietPlan.getDailyCalorieTarget(),
          (int) workoutPlan.getWeeklyPlan().stream().filter(day -> !day.isRestDay()).count());
    }
  }

  // Getters and Setters
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
    generateSummary();
  }

  public DietPlan getDietPlan() {
    return dietPlan;
  }

  public void setDietPlan(DietPlan dietPlan) {
    this.dietPlan = dietPlan;
    generateSummary();
  }

  public WorkoutPlan getWorkoutPlan() {
    return workoutPlan;
  }

  public void setWorkoutPlan(WorkoutPlan workoutPlan) {
    this.workoutPlan = workoutPlan;
    generateSummary();
  }

  public LocalDate getGeneratedDate() {
    return generatedDate;
  }

  public void setGeneratedDate(LocalDate generatedDate) {
    this.generatedDate = generatedDate;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }
}
