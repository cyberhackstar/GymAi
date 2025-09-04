// // Fixed DayMealPlan.java
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
// @Table(name = "day_meal_plans")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class DayMealPlan {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "day_number")
//     private int dayNumber;

//     @Column(name = "day_name")
//     private String dayName;

//     @OneToMany(mappedBy = "dayMealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     @JsonManagedReference
//     private List<Meal> meals = new ArrayList<>();

//     @Column(name = "total_daily_calories")
//     private double totalDailyCalories;

//     @Column(name = "total_daily_protein")
//     private double totalDailyProtein;

//     @Column(name = "total_daily_carbs")
//     private double totalDailyCarbs;

//     @Column(name = "total_daily_fat")
//     private double totalDailyFat;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "diet_plan_id")
//     @JsonBackReference
//     private DietPlan dietPlan;

//     public DayMealPlan(int dayNumber, String dayName) {
//         this.dayNumber = dayNumber;
//         this.dayName = dayName;
//         this.meals = new ArrayList<>();
//     }

//     public void addMeal(Meal meal) {
//         meals.add(meal);
//         meal.setDayMealPlan(this);
//         updateTotals();
//     }

//     private void updateTotals() {
//         this.totalDailyCalories = meals.stream().mapToDouble(Meal::getTotalCalories).sum();
//         this.totalDailyProtein = meals.stream().mapToDouble(Meal::getTotalProtein).sum();
//         this.totalDailyCarbs = meals.stream().mapToDouble(Meal::getTotalCarbs).sum();
//         this.totalDailyFat = meals.stream().mapToDouble(Meal::getTotalFat).sum();
//     }
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
@Table(name = "day_meal_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayMealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_plan_id")
    @JsonBackReference
    private DietPlan dietPlan;

    @OneToMany(mappedBy = "dayMealPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<Meal> meals = new ArrayList<>();

    @Column(name = "day_number")
    private int dayNumber;

    @Column(name = "day_name")
    private String dayName;

    // Daily totals
    @Column(name = "total_daily_calories")
    private double totalDailyCalories;

    @Column(name = "total_daily_protein")
    private double totalDailyProtein;

    @Column(name = "total_daily_carbs")
    private double totalDailyCarbs;

    @Column(name = "total_daily_fat")
    private double totalDailyFat;

    // Helper method to round to 1 decimal place
    private double roundTo1Decimal(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public DayMealPlan(int dayNumber, String dayName) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.meals = new ArrayList<>();
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
        meal.setDayMealPlan(this);
        calculateDailyTotals();
    }

    public void removeMeal(Meal meal) {
        meals.remove(meal);
        meal.setDayMealPlan(null);
        calculateDailyTotals();
    }

    @PrePersist
    @PreUpdate
    public void calculateDailyTotals() {
        this.totalDailyCalories = roundTo1Decimal(
                meals.stream().mapToDouble(Meal::getTotalCalories).sum());

        this.totalDailyProtein = roundTo1Decimal(
                meals.stream().mapToDouble(Meal::getTotalProtein).sum());

        this.totalDailyCarbs = roundTo1Decimal(
                meals.stream().mapToDouble(Meal::getTotalCarbs).sum());

        this.totalDailyFat = roundTo1Decimal(
                meals.stream().mapToDouble(Meal::getTotalFat).sum());
    }

    // Getters with rounding for safety
    public double getTotalDailyCalories() {
        calculateDailyTotals(); // Ensure fresh calculation
        return roundTo1Decimal(totalDailyCalories);
    }

    public double getTotalDailyProtein() {
        calculateDailyTotals(); // Ensure fresh calculation
        return roundTo1Decimal(totalDailyProtein);
    }

    public double getTotalDailyCarbs() {
        calculateDailyTotals(); // Ensure fresh calculation
        return roundTo1Decimal(totalDailyCarbs);
    }

    public double getTotalDailyFat() {
        calculateDailyTotals(); // Ensure fresh calculation
        return roundTo1Decimal(totalDailyFat);
    }
}