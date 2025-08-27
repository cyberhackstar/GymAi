package com.gymai.plan_service.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// WorkoutPlan.java
public class WorkoutPlan {
    private Long userId;
    private List<DayWorkoutPlan> weeklyPlan; // 7 days
    private String planType; // STRENGTH, CARDIO, MIXED, WEIGHT_LOSS, MUSCLE_GAIN
    private String difficultyLevel;
    private LocalDate createdDate;

    public WorkoutPlan() {
        this.weeklyPlan = new ArrayList<>();
        this.createdDate = LocalDate.now();
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<DayWorkoutPlan> getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(List<DayWorkoutPlan> weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
}