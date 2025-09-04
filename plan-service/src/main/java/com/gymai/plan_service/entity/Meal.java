// // Fixed Meal.java
// package com.gymai.plan_service.entity;

// import java.util.ArrayList;
// import java.util.List;

// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonManagedReference;

// import jakarta.persistence.*;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// @Entity
// @Table(name = "meals")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class Meal {

//   @Id
//   @GeneratedValue(strategy = GenerationType.IDENTITY)
//   private Long id;

//   @Column(name = "meal_type")
//   private String mealType;

//   @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//   @JsonManagedReference
//   private List<FoodItem> foodItems = new ArrayList<>();

//   @Column(name = "total_calories")
//   private double totalCalories;

//   @Column(name = "total_protein")
//   private double totalProtein;

//   @Column(name = "total_carbs")
//   private double totalCarbs;

//   @Column(name = "total_fat")
//   private double totalFat;

//   @ManyToOne(fetch = FetchType.LAZY)
//   @JoinColumn(name = "day_meal_plan_id")
//   @JsonBackReference
//   private DayMealPlan dayMealPlan;

//   public Meal(String mealType) {
//     this.mealType = mealType;
//     this.foodItems = new ArrayList<>();
//   }

//   public void addFoodItem(FoodItem foodItem) {
//     foodItems.add(foodItem);
//     foodItem.setMeal(this);
//     updateTotals();
//   }

//   private void updateTotals() {
//     this.totalCalories = foodItems.stream().mapToDouble(FoodItem::getCalories).sum();
//     this.totalProtein = foodItems.stream().mapToDouble(FoodItem::getProtein).sum();
//     this.totalCarbs = foodItems.stream().mapToDouble(FoodItem::getCarbs).sum();
//     this.totalFat = foodItems.stream().mapToDouble(FoodItem::getFat).sum();
//   }
// }

package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "meals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "day_meal_plan_id")
  @JsonBackReference
  private DayMealPlan dayMealPlan;

  @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JsonManagedReference
  private List<FoodItem> foodItems = new ArrayList<>();

  @Column(name = "meal_type")
  private String mealType;

  // Calculated totals
  @Column(name = "total_calories")
  private double totalCalories;

  @Column(name = "total_protein")
  private double totalProtein;

  @Column(name = "total_carbs")
  private double totalCarbs;

  @Column(name = "total_fat")
  private double totalFat;

  // Helper method to round to 1 decimal place
  private double roundTo1Decimal(double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

  public Meal(String mealType) {
    this.mealType = mealType;
    this.foodItems = new ArrayList<>();
  }

  public void addFoodItem(FoodItem foodItem) {
    foodItems.add(foodItem);
    foodItem.setMeal(this);
    calculateTotals();
  }

  public void removeFoodItem(FoodItem foodItem) {
    foodItems.remove(foodItem);
    foodItem.setMeal(null);
    calculateTotals();
  }

  @PrePersist
  @PreUpdate
  public void calculateTotals() {
    this.totalCalories = roundTo1Decimal(
        foodItems.stream().mapToDouble(FoodItem::getCalories).sum());

    this.totalProtein = roundTo1Decimal(
        foodItems.stream().mapToDouble(FoodItem::getProtein).sum());

    this.totalCarbs = roundTo1Decimal(
        foodItems.stream().mapToDouble(FoodItem::getCarbs).sum());

    this.totalFat = roundTo1Decimal(
        foodItems.stream().mapToDouble(FoodItem::getFat).sum());
  }

  // Getters with rounding for safety
  public double getTotalCalories() {
    calculateTotals(); // Ensure fresh calculation
    return roundTo1Decimal(totalCalories);
  }

  public double getTotalProtein() {
    calculateTotals(); // Ensure fresh calculation
    return roundTo1Decimal(totalProtein);
  }

  public double getTotalCarbs() {
    calculateTotals(); // Ensure fresh calculation
    return roundTo1Decimal(totalCarbs);
  }

  public double getTotalFat() {
    calculateTotals(); // Ensure fresh calculation
    return roundTo1Decimal(totalFat);
  }
}