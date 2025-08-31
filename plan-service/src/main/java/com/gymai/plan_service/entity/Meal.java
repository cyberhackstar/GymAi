// Fixed Meal.java
package com.gymai.plan_service.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "meal_type")
  private String mealType;

  @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JsonManagedReference
  private List<FoodItem> foodItems = new ArrayList<>();

  @Column(name = "total_calories")
  private double totalCalories;

  @Column(name = "total_protein")
  private double totalProtein;

  @Column(name = "total_carbs")
  private double totalCarbs;

  @Column(name = "total_fat")
  private double totalFat;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "day_meal_plan_id")
  @JsonBackReference
  private DayMealPlan dayMealPlan;

  public Meal(String mealType) {
    this.mealType = mealType;
    this.foodItems = new ArrayList<>();
  }

  public void addFoodItem(FoodItem foodItem) {
    foodItems.add(foodItem);
    foodItem.setMeal(this);
    updateTotals();
  }

  private void updateTotals() {
    this.totalCalories = foodItems.stream().mapToDouble(FoodItem::getCalories).sum();
    this.totalProtein = foodItems.stream().mapToDouble(FoodItem::getProtein).sum();
    this.totalCarbs = foodItems.stream().mapToDouble(FoodItem::getCarbs).sum();
    this.totalFat = foodItems.stream().mapToDouble(FoodItem::getFat).sum();
  }
}