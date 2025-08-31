// Updated FoodItem.java - Now a JPA Entity
package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id")
    private Food food;

    private double quantity;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    @JsonBackReference
    private Meal meal;

    public FoodItem(Food food, double quantity) {
        this.food = food;
        this.quantity = quantity;
        calculateNutrition();
    }

    private void calculateNutrition() {
        double factor = quantity / 100.0;
        this.calories = food.getCaloriesPer100g() * factor;
        this.protein = food.getProteinPer100g() * factor;
        this.carbs = food.getCarbsPer100g() * factor;
        this.fat = food.getFatPer100g() * factor;
        this.fiber = food.getFiberPer100g() * factor;
    }
}