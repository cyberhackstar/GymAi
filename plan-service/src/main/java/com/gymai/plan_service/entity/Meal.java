package com.gymai.plan_service.entity;

import java.util.ArrayList;
import java.util.List;

// Meal.java
public class Meal {
  private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK
  private List<FoodItem> foodItems;
  private double totalCalories;
  private double totalProtein;
  private double totalCarbs;
  private double totalFat;

  public Meal() {
    this.foodItems = new ArrayList<>();
  }

  public Meal(String mealType) {
    this.mealType = mealType;
    this.foodItems = new ArrayList<>();
  }

  public void addFoodItem(FoodItem foodItem) {
    this.foodItems.add(foodItem);
    calculateTotals();
  }

  private void calculateTotals() {
    this.totalCalories = foodItems.stream()
        .mapToDouble(item -> item.getCalories())
        .sum();
    this.totalProtein = foodItems.stream()
        .mapToDouble(item -> item.getProtein())
        .sum();
    this.totalCarbs = foodItems.stream()
        .mapToDouble(item -> item.getCarbs())
        .sum();
    this.totalFat = foodItems.stream()
        .mapToDouble(item -> item.getFat())
        .sum();
  }

  // Getters and setters
  public String getMealType() {
    return mealType;
  }

  public void setMealType(String mealType) {
    this.mealType = mealType;
  }

  public List<FoodItem> getFoodItems() {
    return foodItems;
  }

  public void setFoodItems(List<FoodItem> foodItems) {
    this.foodItems = foodItems;
    calculateTotals();
  }

  public double getTotalCalories() {
    return totalCalories;
  }

  public double getTotalProtein() {
    return totalProtein;
  }

  public double getTotalCarbs() {
    return totalCarbs;
  }

  public double getTotalFat() {
    return totalFat;
  }
}