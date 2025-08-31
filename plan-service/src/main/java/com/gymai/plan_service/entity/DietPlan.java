// Fixed DietPlan.java
package com.gymai.plan_service.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "diet_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "dietPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<DayMealPlan> dailyPlans = new ArrayList<>();

    @Column(name = "daily_calorie_target")
    private double dailyCalorieTarget;

    @Column(name = "daily_protein_target")
    private double dailyProteinTarget;

    @Column(name = "daily_carbs_target")
    private double dailyCarbsTarget;

    @Column(name = "daily_fat_target")
    private double dailyFatTarget;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }

    // Helper method to add day meal plan
    public void addDayMealPlan(DayMealPlan dayMealPlan) {
        dailyPlans.add(dayMealPlan);
        dayMealPlan.setDietPlan(this);
    }
}