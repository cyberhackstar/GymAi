
package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    @JsonBackReference
    private Meal meal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(name = "quantity")
    private double quantity; // in grams

    // Calculated nutritional values per serving
    @Column(name = "calories")
    private double calories;

    @Column(name = "protein")
    private double protein;

    @Column(name = "carbs")
    private double carbs;

    @Column(name = "fat")
    private double fat;

    @Column(name = "fiber")
    private double fiber;

    // Helper method to round to 1 decimal place
    private double roundTo1Decimal(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public FoodItem(Food food, double quantity) {
        this.food = food;
        this.quantity = roundTo1Decimal(quantity);
        calculateNutrition();
    }

    @PrePersist
    @PreUpdate
    public void calculateNutrition() {
        if (food != null && quantity > 0) {
            double factor = quantity / 100.0; // Convert to per 100g factor

            this.calories = roundTo1Decimal(food.getCaloriesPer100g() * factor);
            this.protein = roundTo1Decimal(food.getProteinPer100g() * factor);
            this.carbs = roundTo1Decimal(food.getCarbsPer100g() * factor);
            this.fat = roundTo1Decimal(food.getFatPer100g() * factor);
            this.fiber = roundTo1Decimal(food.getFiberPer100g() * factor);
        }
    }

    // Setters that trigger recalculation
    public void setQuantity(double quantity) {
        this.quantity = roundTo1Decimal(quantity);
        calculateNutrition();
    }

    public void setFood(Food food) {
        this.food = food;
        calculateNutrition();
    }

    // Getters with rounding for safety
    public double getCalories() {
        return roundTo1Decimal(calories);
    }

    public double getProtein() {
        return roundTo1Decimal(protein);
    }

    public double getCarbs() {
        return roundTo1Decimal(carbs);
    }

    public double getFat() {
        return roundTo1Decimal(fat);
    }

    public double getFiber() {
        return roundTo1Decimal(fiber);
    }

    public double getQuantity() {
        return roundTo1Decimal(quantity);
    }
}