package com.gymai.plan_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Food.java
@Entity
@Table(name = "foods")
public class Food {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private double caloriesPer100g;
  private double proteinPer100g;
  private double carbsPer100g;
  private double fatPer100g;
  private double fiberPer100g;
  private String dietType; // VEG, NON_VEG, VEGAN
  private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK
  private String category; // GRAINS, PROTEIN, VEGETABLES, FRUITS, DAIRY, NUTS

  // Constructors, getters and setters
  public Food() {
  }

  public Food(String name, double calories, double protein, double carbs, double fat,
      double fiber, String dietType, String mealType, String category) {
    this.name = name;
    this.caloriesPer100g = calories;
    this.proteinPer100g = protein;
    this.carbsPer100g = carbs;
    this.fatPer100g = fat;
    this.fiberPer100g = fiber;
    this.dietType = dietType;
    this.mealType = mealType;
    this.category = category;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getCaloriesPer100g() {
    return caloriesPer100g;
  }

  public void setCaloriesPer100g(double caloriesPer100g) {
    this.caloriesPer100g = caloriesPer100g;
  }

  public double getProteinPer100g() {
    return proteinPer100g;
  }

  public void setProteinPer100g(double proteinPer100g) {
    this.proteinPer100g = proteinPer100g;
  }

  public double getCarbsPer100g() {
    return carbsPer100g;
  }

  public void setCarbsPer100g(double carbsPer100g) {
    this.carbsPer100g = carbsPer100g;
  }

  public double getFatPer100g() {
    return fatPer100g;
  }

  public void setFatPer100g(double fatPer100g) {
    this.fatPer100g = fatPer100g;
  }

  public double getFiberPer100g() {
    return fiberPer100g;
  }

  public void setFiberPer100g(double fiberPer100g) {
    this.fiberPer100g = fiberPer100g;
  }

  public String getDietType() {
    return dietType;
  }

  public void setDietType(String dietType) {
    this.dietType = dietType;
  }

  public String getMealType() {
    return mealType;
  }

  public void setMealType(String mealType) {
    this.mealType = mealType;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
