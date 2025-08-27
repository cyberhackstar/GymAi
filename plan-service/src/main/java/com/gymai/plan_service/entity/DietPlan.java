package com.gymai.plan_service.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DietPlan {
    private Long userId;
    private List<DayMealPlan> dailyPlans; // 7 days
    private double dailyCalorieTarget;
    private double dailyProteinTarget;
    private double dailyCarbsTarget;
    private double dailyFatTarget;
    private LocalDate createdDate;

    public DietPlan() {
        this.dailyPlans = new ArrayList<>();
        this.createdDate = LocalDate.now();
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<DayMealPlan> getDailyPlans() {
        return dailyPlans;
    }

    public void setDailyPlans(List<DayMealPlan> dailyPlans) {
        this.dailyPlans = dailyPlans;
    }

    public double getDailyCalorieTarget() {
        return dailyCalorieTarget;
    }

    public void setDailyCalorieTarget(double dailyCalorieTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
    }

    public double getDailyProteinTarget() {
        return dailyProteinTarget;
    }

    public void setDailyProteinTarget(double dailyProteinTarget) {
        this.dailyProteinTarget = dailyProteinTarget;
    }

    public double getDailyCarbsTarget() {
        return dailyCarbsTarget;
    }

    public void setDailyCarbsTarget(double dailyCarbsTarget) {
        this.dailyCarbsTarget = dailyCarbsTarget;
    }

    public double getDailyFatTarget() {
        return dailyFatTarget;
    }

    public void setDailyFatTarget(double dailyFatTarget) {
        this.dailyFatTarget = dailyFatTarget;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
}