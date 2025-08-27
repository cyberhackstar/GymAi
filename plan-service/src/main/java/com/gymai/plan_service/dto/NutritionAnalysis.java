package com.gymai.plan_service.dto;

public class NutritionAnalysis {
  private Long userId;
  private double dailyCalories;
  private double dailyProtein;
  private double dailyCarbs;
  private double dailyFat;
  private double bmr;
  private double tdee;
  private String recommendation;

  // Constructor
  public NutritionAnalysis() {
    generateRecommendation();
  }

  private void generateRecommendation() {
    if (dailyCalories > 0) {
      if (dailyCalories < 1200) {
        this.recommendation = "Consider increasing caloric intake for sustainable health.";
      } else if (dailyCalories > 3000) {
        this.recommendation = "Monitor portion sizes to avoid excessive calorie intake.";
      } else {
        this.recommendation = "Caloric intake is within healthy range.";
      }
    }
  }

  // Getters and Setters
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public double getDailyCalories() {
    return dailyCalories;
  }

  public void setDailyCalories(double dailyCalories) {
    this.dailyCalories = dailyCalories;
    generateRecommendation();
  }

  public double getDailyProtein() {
    return dailyProtein;
  }

  public void setDailyProtein(double dailyProtein) {
    this.dailyProtein = dailyProtein;
  }

  public double getDailyCarbs() {
    return dailyCarbs;
  }

  public void setDailyCarbs(double dailyCarbs) {
    this.dailyCarbs = dailyCarbs;
  }

  public double getDailyFat() {
    return dailyFat;
  }

  public void setDailyFat(double dailyFat) {
    this.dailyFat = dailyFat;
  }

  public double getBmr() {
    return bmr;
  }

  public void setBmr(double bmr) {
    this.bmr = bmr;
  }

  public double getTdee() {
    return tdee;
  }

  public void setTdee(double tdee) {
    this.tdee = tdee;
  }

  public String getRecommendation() {
    return recommendation;
  }

  public void setRecommendation(String recommendation) {
    this.recommendation = recommendation;
  }
}