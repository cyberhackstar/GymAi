package com.gymai.plan_service.service;

import org.springframework.stereotype.Service;
import com.gymai.plan_service.entity.User;
import lombok.extern.slf4j.Slf4j;

// NutritionCalculatorService.java
@Service
@Slf4j
public class NutritionCalculatorService {

    public NutritionalNeeds calculateNutritionalNeeds(User user) {
        log.info("Calculating nutritional needs for user: {}", user);

        double bmr = calculateBMR(user);
        log.debug("Calculated BMR: {}", bmr);

        double tdee = calculateTDEE(bmr, user.getActivityLevel());
        log.debug("Calculated TDEE (with activity level {}): {}", user.getActivityLevel(), tdee);

        double targetCalories = adjustCaloriesForGoal(tdee, user.getGoal());
        log.info("Adjusted target calories for goal {}: {}", user.getGoal(), targetCalories);

        // Calculate macronutrient targets
        MacroTargets macros = calculateMacroTargets(targetCalories, user.getGoal());
        log.info("Macro targets -> Protein: {}g, Carbs: {}g, Fat: {}g", macros.protein, macros.carbs, macros.fat);

        return new NutritionalNeeds(targetCalories, macros.protein, macros.carbs, macros.fat);
    }

    private double calculateBMR(User user) {
        if ("MALE".equalsIgnoreCase(user.getGender())) {
            return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
        } else {
            return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
        }
    }

    private double calculateTDEE(double bmr, String activityLevel) {
        double activityMultiplier;
        switch (activityLevel.toUpperCase()) {
            case "SEDENTARY":
                activityMultiplier = 1.2;
                break;
            case "LIGHTLY_ACTIVE":
                activityMultiplier = 1.375;
                break;
            case "MODERATELY_ACTIVE":
                activityMultiplier = 1.55;
                break;
            case "VERY_ACTIVE":
                activityMultiplier = 1.725;
                break;
            case "EXTREMELY_ACTIVE":
                activityMultiplier = 1.9;
                break;
            default:
                log.warn("Unknown activity level: {}. Defaulting to SEDENTARY (1.2)", activityLevel);
                activityMultiplier = 1.2;
        }
        return bmr * activityMultiplier;
    }

    private double adjustCaloriesForGoal(double tdee, String goal) {
        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                return tdee - 500; // deficit
            case "WEIGHT_GAIN":
                return tdee + 500; // surplus
            case "MUSCLE_GAIN":
                return tdee + 300; // surplus
            case "MAINTENANCE":
            default:
                return tdee;
        }
    }

    private MacroTargets calculateMacroTargets(double calories, String goal) {
        double protein, carbs, fat;

        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                protein = calories * 0.35 / 4;
                carbs = calories * 0.35 / 4;
                fat = calories * 0.30 / 9;
                break;
            case "MUSCLE_GAIN":
                protein = calories * 0.30 / 4;
                carbs = calories * 0.45 / 4;
                fat = calories * 0.25 / 9;
                break;
            case "WEIGHT_GAIN":
                protein = calories * 0.25 / 4;
                carbs = calories * 0.50 / 4;
                fat = calories * 0.25 / 9;
                break;
            case "MAINTENANCE":
            default:
                protein = calories * 0.25 / 4;
                carbs = calories * 0.45 / 4;
                fat = calories * 0.30 / 9;
        }

        return new MacroTargets(protein, carbs, fat);
    }

    // Inner classes for data structure
    public static class NutritionalNeeds {
        public final double calories;
        public final double protein;
        public final double carbs;
        public final double fat;

        public NutritionalNeeds(double calories, double protein, double carbs, double fat) {
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }

    private static class MacroTargets {
        public final double protein;
        public final double carbs;
        public final double fat;

        public MacroTargets(double protein, double carbs, double fat) {
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }
}
