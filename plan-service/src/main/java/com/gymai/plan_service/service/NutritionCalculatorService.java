package com.gymai.plan_service.service;

import org.springframework.stereotype.Service;
import com.gymai.plan_service.entity.User;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class NutritionCalculatorService {

    // Helper method to round to 1 decimal place
    private double roundTo1Decimal(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public NutritionalNeeds calculateNutritionalNeeds(User user) {
        log.info("Calculating nutritional needs for user: {}", user);

        double bmr = calculateBMR(user);
        bmr = roundTo1Decimal(bmr);
        log.debug("Calculated BMR: {}", bmr);

        double tdee = calculateTDEE(bmr, user.getActivityLevel());
        tdee = roundTo1Decimal(tdee);
        log.debug("Calculated TDEE (with activity level {}): {}", user.getActivityLevel(), tdee);

        double targetCalories = adjustCaloriesForGoal(tdee, user.getGoal());
        targetCalories = roundTo1Decimal(targetCalories);
        log.info("Adjusted target calories for goal {}: {}", user.getGoal(), targetCalories);

        // Calculate macronutrient targets
        MacroTargets macros = calculateMacroTargets(user, targetCalories, user.getGoal());
        log.info("Macro targets -> Protein: {}g, Carbs: {}g, Fat: {}g", macros.protein, macros.carbs, macros.fat);

        return new NutritionalNeeds(
                targetCalories,
                macros.protein,
                macros.carbs,
                macros.fat);
    }

    private double calculateBMR(User user) {
        // Mifflin-St Jeor Equation (most accurate widely used formula)
        if ("MALE".equalsIgnoreCase(user.getGender())) {
            return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
        } else {
            return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
        }
    }

    private double calculateTDEE(double bmr, String activityLevel) {
        double activityMultiplier;
        switch (activityLevel.toUpperCase()) {
            case "SEDENTARY": // little to no exercise
                activityMultiplier = 1.2;
                break;
            case "LIGHTLY_ACTIVE": // light exercise 1–3 days/week
                activityMultiplier = 1.375;
                break;
            case "MODERATELY_ACTIVE": // moderate exercise 3–5 days/week
                activityMultiplier = 1.55;
                break;
            case "VERY_ACTIVE": // hard exercise 6–7 days/week
                activityMultiplier = 1.725;
                break;
            case "EXTREMELY_ACTIVE": // very intense training / physical job
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
                return tdee - 500; // ~0.45 kg loss per week
            case "WEIGHT_GAIN":
                return tdee + 500; // ~0.45 kg gain per week
            case "MUSCLE_GAIN":
                return tdee + 300; // lean bulk approach
            case "MAINTENANCE":
            default:
                return tdee;
        }
    }

    private MacroTargets calculateMacroTargets(User user, double calories, String goal) {
        double protein, carbs, fat;

        // Protein recommendation based on body weight (grams/kg)
        double proteinPerKg;
        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                proteinPerKg = 2.0; // higher protein to preserve muscle
                break;
            case "MUSCLE_GAIN":
                proteinPerKg = 1.8; // optimal for hypertrophy
                break;
            case "WEIGHT_GAIN":
                proteinPerKg = 1.6; // moderate surplus protein
                break;
            case "MAINTENANCE":
            default:
                proteinPerKg = 1.6;
        }

        protein = proteinPerKg * user.getWeight();
        protein = roundTo1Decimal(protein);
        double proteinCalories = protein * 4;

        // Fat: 25% of calories
        double fatCalories = calories * 0.25;
        fat = fatCalories / 9;
        fat = roundTo1Decimal(fat);

        // Carbs = remaining calories
        double carbsCalories = calories - (proteinCalories + fatCalories);
        carbs = carbsCalories / 4;
        carbs = roundTo1Decimal(carbs);

        return new MacroTargets(protein, carbs, fat);
    }

    // Inner classes for data structure
    public static class NutritionalNeeds {
        public double calories;
        public double protein;
        public double carbs;
        public double fat;

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