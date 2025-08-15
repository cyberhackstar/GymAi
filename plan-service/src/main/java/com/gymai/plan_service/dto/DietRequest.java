package com.gymai.plan_service.dto;

public class DietRequest {
  private Long userId;
  private String name;
  private String email;
  private int age;
  private int height; // cm
  private int weight; // kg
  private String gender; // male/female
  private String goal; // lose, maintain, gain
  private String activityLevel; // sedentary, active, etc.
  private String preference; // vegetarian, vegan, etc.
  // Getters and setters

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getGoal() {
    return goal;
  }

  public void setGoal(String goal) {
    this.goal = goal;
  }

  public String getActivityLevel() {
    return activityLevel;
  }

  public void setActivityLevel(String activityLevel) {
    this.activityLevel = activityLevel;
  }

  public String getPreference() {
    return preference;
  }

  public void setPreference(String preference) {
    this.preference = preference;
  }

}
