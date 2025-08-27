package com.gymai.plan_service.entity;

// FoodItem.java
public class FoodItem {
    private Food food;
    private double quantity; // in grams
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    public FoodItem(Food food, double quantity) {
        this.food = food;
        this.quantity = quantity;
        calculateNutrition();
    }

    private void calculateNutrition() {
        double factor = quantity / 100.0; // since nutrition is per 100g
        this.calories = food.getCaloriesPer100g() * factor;
        this.protein = food.getProteinPer100g() * factor;
        this.carbs = food.getCarbsPer100g() * factor;
        this.fat = food.getFatPer100g() * factor;
        this.fiber = food.getFiberPer100g() * factor;
    }

    // Getters and setters
    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        calculateNutrition();
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }

    public double getFiber() {
        return fiber;
    }
}