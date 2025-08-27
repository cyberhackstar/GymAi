package com.gymai.plan_service.dto;

import com.gymai.plan_service.entity.DietPlan;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.entity.WorkoutPlan;

public class UserPlansResponse {
  private User user;
  private DietPlan dietPlan;
  private WorkoutPlan workoutPlan;

  // Getters and Setters
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public DietPlan getDietPlan() {
    return dietPlan;
  }

  public void setDietPlan(DietPlan dietPlan) {
    this.dietPlan = dietPlan;
  }

  public WorkoutPlan getWorkoutPlan() {
    return workoutPlan;
  }

  public void setWorkoutPlan(WorkoutPlan workoutPlan) {
    this.workoutPlan = workoutPlan;
  }
}
