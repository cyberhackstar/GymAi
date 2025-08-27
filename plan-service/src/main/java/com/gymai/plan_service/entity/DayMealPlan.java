package com.gymai.plan_service.entity;

import java.util.ArrayList;
import java.util.List;

// DayMealPlan.java
public class DayMealPlan {
    private int dayNumber;
    private String dayName;
    private List<Meal> meals;
    private double totalDailyCalories;
    private double totalDailyProtein;
    private double totalDailyCarbs;
    private double totalDailyFat;

    public DayMealPlan(int dayNumber, String dayName) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.meals = new ArrayList<>();
    }

    public void addMeal(Meal meal) {
        this.meals.add(meal);
        calculateDailyTotals();
    }

    private void calculateDailyTotals() {
        this.totalDailyCalories = meals.stream().mapToDouble(Meal::getTotalCalories).sum();
        this.totalDailyProtein = meals.stream().mapToDouble(Meal::getTotalProtein).sum();
        this.totalDailyCarbs = meals.stream().mapToDouble(Meal::getTotalCarbs).sum();
        this.totalDailyFat = meals.stream().mapToDouble(Meal::getTotalFat).sum();
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

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        calculateDailyTotals();
    }

    public double getTotalDailyCalories() {
        return totalDailyCalories;
    }

    public double getTotalDailyProtein() {
        return totalDailyProtein;
    }

    public double getTotalDailyCarbs() {
        return totalDailyCarbs;
    }

    public double getTotalDailyFat() {
        return totalDailyFat;
    }
}