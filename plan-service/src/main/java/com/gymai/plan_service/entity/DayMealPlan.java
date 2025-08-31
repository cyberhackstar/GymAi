// Fixed DayMealPlan.java
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
@Table(name = "day_meal_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_number")
    private int dayNumber;

    @Column(name = "day_name")
    private String dayName;

    @OneToMany(mappedBy = "dayMealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Meal> meals = new ArrayList<>();

    @Column(name = "total_daily_calories")
    private double totalDailyCalories;

    @Column(name = "total_daily_protein")
    private double totalDailyProtein;

    @Column(name = "total_daily_carbs")
    private double totalDailyCarbs;

    @Column(name = "total_daily_fat")
    private double totalDailyFat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id")
    @JsonBackReference
    private DietPlan dietPlan;

    public DayMealPlan(int dayNumber, String dayName) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.meals = new ArrayList<>();
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
        meal.setDayMealPlan(this);
        updateTotals();
    }

    private void updateTotals() {
        this.totalDailyCalories = meals.stream().mapToDouble(Meal::getTotalCalories).sum();
        this.totalDailyProtein = meals.stream().mapToDouble(Meal::getTotalProtein).sum();
        this.totalDailyCarbs = meals.stream().mapToDouble(Meal::getTotalCarbs).sum();
        this.totalDailyFat = meals.stream().mapToDouble(Meal::getTotalFat).sum();
    }
}