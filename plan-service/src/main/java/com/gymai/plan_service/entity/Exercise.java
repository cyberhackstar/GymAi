package com.gymai.plan_service.entity;

import jakarta.persistence.*;

// Exercise.java
@Entity
@Table(name = "exercises")
public class Exercise {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String category; // CARDIO, STRENGTH, FLEXIBILITY, SPORTS
  private String muscleGroup; // CHEST, BACK, LEGS, ARMS, SHOULDERS, CORE, FULL_BODY
  private String equipment; // NONE, DUMBBELL, BARBELL, MACHINE, RESISTANCE_BAND
  private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED
  private double caloriesBurnedPerMinute; // approximate
  private String description;
  private String instructions;

  // Constructors, getters and setters
  public Exercise() {
  }

  public Exercise(String name, String category, String muscleGroup, String equipment,
      String difficulty, double caloriesBurnedPerMinute, String description, String instructions) {
    this.name = name;
    this.category = category;
    this.muscleGroup = muscleGroup;
    this.equipment = equipment;
    this.difficulty = difficulty;
    this.caloriesBurnedPerMinute = caloriesBurnedPerMinute;
    this.description = description;
    this.instructions = instructions;
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getMuscleGroup() {
    return muscleGroup;
  }

  public void setMuscleGroup(String muscleGroup) {
    this.muscleGroup = muscleGroup;
  }

  public String getEquipment() {
    return equipment;
  }

  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }

  public String getDifficulty() {
    return difficulty;
  }

  public void setDifficulty(String difficulty) {
    this.difficulty = difficulty;
  }

  public double getCaloriesBurnedPerMinute() {
    return caloriesBurnedPerMinute;
  }

  public void setCaloriesBurnedPerMinute(double caloriesBurnedPerMinute) {
    this.caloriesBurnedPerMinute = caloriesBurnedPerMinute;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }
}
