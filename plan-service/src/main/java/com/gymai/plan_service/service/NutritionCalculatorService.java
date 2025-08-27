package com.gymai.plan_service.service;

import org.springframework.stereotype.Service;

import com.gymai.plan_service.entity.User;

// NutritionCalculatorService.java
@Service
public class NutritionCalculatorService {

    public NutritionalNeeds calculateNutritionalNeeds(User user) {
        double bmr = calculateBMR(user);
        double tdee = calculateTDEE(bmr, user.getActivityLevel());
        double targetCalories = adjustCaloriesForGoal(tdee, user.getGoal());

        // Calculate macronutrient targets
        MacroTargets macros = calculateMacroTargets(targetCalories, user.getGoal());

        return new NutritionalNeeds(targetCalories, macros.protein, macros.carbs, macros.fat);
    }

    private double calculateBMR(User user) {
        // Using Mifflin-St Jeor Equation
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
                activityMultiplier = 1.2;
        }
        return bmr * activityMultiplier;
    }

    private double adjustCaloriesForGoal(double tdee, String goal) {
        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                return tdee - 500; // 500 calorie deficit
            case "WEIGHT_GAIN":
                return tdee + 500; // 500 calorie surplus
            case "MUSCLE_GAIN":
                return tdee + 300; // 300 calorie surplus
            case "MAINTENANCE":
            default:
                return tdee;
        }
    }

    private MacroTargets calculateMacroTargets(double calories, String goal) {
        double protein, carbs, fat;

        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                // High protein, moderate carbs, low fat
                protein = calories * 0.35 / 4; // 35% protein
                carbs = calories * 0.35 / 4; // 35% carbs
                fat = calories * 0.30 / 9; // 30% fat
                break;
            case "MUSCLE_GAIN":
                // High protein, high carbs, moderate fat
                protein = calories * 0.30 / 4; // 30% protein
                carbs = calories * 0.45 / 4; // 45% carbs
                fat = calories * 0.25 / 9; // 25% fat
                break;
            case "WEIGHT_GAIN":
                // Moderate protein, high carbs, moderate fat
                protein = calories * 0.25 / 4; // 25% protein
                carbs = calories * 0.50 / 4; // 50% carbs
                fat = calories * 0.25 / 9; // 25% fat
                break;
            case "MAINTENANCE":
            default:
                // Balanced macros
                protein = calories * 0.25 / 4; // 25% protein
                carbs = calories * 0.45 / 4; // 45% carbs
                fat = calories * 0.30 / 9; // 30% fat
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
