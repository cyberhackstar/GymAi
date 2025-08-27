package com.gymai.plan_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gymai.plan_service.entity.DayMealPlan;
import com.gymai.plan_service.entity.DietPlan;
import com.gymai.plan_service.entity.Food;
import com.gymai.plan_service.entity.FoodItem;
import com.gymai.plan_service.entity.Meal;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.repository.FoodRepository;

// DietPlanService.java
@Service
public class DietPlanService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private NutritionCalculatorService nutritionCalculator;

    public DietPlan generateCustomDietPlan(User user) {
        // Calculate nutritional needs
        NutritionCalculatorService.NutritionalNeeds needs = nutritionCalculator.calculateNutritionalNeeds(user);

        // Create diet plan
        DietPlan dietPlan = new DietPlan();
        dietPlan.setUserId(user.getUserId());
        dietPlan.setDailyCalorieTarget(needs.calories);
        dietPlan.setDailyProteinTarget(needs.protein);
        dietPlan.setDailyCarbsTarget(needs.carbs);
        dietPlan.setDailyFatTarget(needs.fat);

        // Generate 7-day meal plan
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");

        for (int i = 0; i < 7; i++) {
            DayMealPlan dayPlan = generateDayMealPlan(i + 1, dayNames.get(i), user, needs);
            dietPlan.getDailyPlans().add(dayPlan);
        }

        return dietPlan;
    }

    private DayMealPlan generateDayMealPlan(int dayNumber, String dayName, User user,
            NutritionCalculatorService.NutritionalNeeds needs) {
        DayMealPlan dayPlan = new DayMealPlan(dayNumber, dayName);

        // Calorie distribution across meals
        double breakfastCalories = needs.calories * 0.25; // 25%
        double lunchCalories = needs.calories * 0.35; // 35%
        double dinnerCalories = needs.calories * 0.30; // 30%
        double snackCalories = needs.calories * 0.10; // 10%

        // Generate meals
        Meal breakfast = generateMeal("BREAKFAST", breakfastCalories, user);
        Meal lunch = generateMeal("LUNCH", lunchCalories, user);
        Meal dinner = generateMeal("DINNER", dinnerCalories, user);
        Meal snack = generateMeal("SNACK", snackCalories, user);

        dayPlan.addMeal(breakfast);
        dayPlan.addMeal(lunch);
        dayPlan.addMeal(dinner);
        dayPlan.addMeal(snack);

        return dayPlan;
    }

    private Meal generateMeal(String mealType, double targetCalories, User user) {
        Meal meal = new Meal(mealType);

        // Get suitable foods for this meal type and user preference
        List<String> dietTypes = getDietTypes(user.getPreference());
        List<Food> availableFoods = foodRepository.findByDietTypeInAndMealType(dietTypes, mealType);

        if (availableFoods.isEmpty()) {
            // Fallback to any foods of the diet type
            availableFoods = foodRepository.findByDietTypeAndMealType(
                    user.getPreference().toUpperCase(), "LUNCH");
        }

        // Create meal based on meal type
        switch (mealType.toUpperCase()) {
            case "BREAKFAST":
                meal = generateBreakfast(availableFoods, targetCalories, user);
                break;
            case "LUNCH":
            case "DINNER":
                meal = generateMainMeal(mealType, availableFoods, targetCalories, user);
                break;
            case "SNACK":
                meal = generateSnack(availableFoods, targetCalories, user);
                break;
        }

        return meal;
    }

    private Meal generateBreakfast(List<Food> availableFoods, double targetCalories, User user) {
        Meal breakfast = new Meal("BREAKFAST");

        // Try to include: grains + protein + fruits
        Optional<Food> grains = availableFoods.stream()
                .filter(f -> "GRAINS".equals(f.getCategory()))
                .findFirst();

        Optional<Food> protein = availableFoods.stream()
                .filter(f -> "PROTEIN".equals(f.getCategory()) || "DAIRY".equals(f.getCategory()))
                .findFirst();

        Optional<Food> fruits = availableFoods.stream()
                .filter(f -> "FRUITS".equals(f.getCategory()))
                .findFirst();

        double remainingCalories = targetCalories;

        // Add grains (40% of breakfast calories)
        if (grains.isPresent()) {
            double grainsCalories = targetCalories * 0.4;
            double quantity = (grainsCalories / grains.get().getCaloriesPer100g()) * 100;
            breakfast.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
        }

        // Add protein (35% of breakfast calories)
        if (protein.isPresent()) {
            double proteinCalories = targetCalories * 0.35;
            double quantity = (proteinCalories / protein.get().getCaloriesPer100g()) * 100;
            breakfast.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
        }

        // Add fruits (remaining calories)
        if (fruits.isPresent() && remainingCalories > 0) {
            double quantity = (remainingCalories / fruits.get().getCaloriesPer100g()) * 100;
            breakfast.addFoodItem(new FoodItem(fruits.get(), Math.max(quantity, 100))); // Min 100g
        }

        return breakfast;
    }

    private Meal generateMainMeal(String mealType, List<Food> availableFoods,
            double targetCalories, User user) {
        Meal meal = new Meal(mealType);

        // Try to include: grains + protein + vegetables
        Optional<Food> grains = availableFoods.stream()
                .filter(f -> "GRAINS".equals(f.getCategory()))
                .skip((long) (Math.random() * availableFoods.stream()
                        .filter(f -> "GRAINS".equals(f.getCategory())).count()))
                .findFirst();

        Optional<Food> protein = availableFoods.stream()
                .filter(f -> "PROTEIN".equals(f.getCategory()))
                .skip((long) (Math.random() * availableFoods.stream()
                        .filter(f -> "PROTEIN".equals(f.getCategory())).count()))
                .findFirst();

        Optional<Food> vegetables = availableFoods.stream()
                .filter(f -> "VEGETABLES".equals(f.getCategory()))
                .skip((long) (Math.random() * availableFoods.stream()
                        .filter(f -> "VEGETABLES".equals(f.getCategory())).count()))
                .findFirst();

        double remainingCalories = targetCalories;

        // Add grains (35% of meal calories)
        if (grains.isPresent()) {
            double grainsCalories = targetCalories * 0.35;
            double quantity = (grainsCalories / grains.get().getCaloriesPer100g()) * 100;
            meal.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
        }

        // Add protein (45% of meal calories)
        if (protein.isPresent()) {
            double proteinCalories = targetCalories * 0.45;
            double quantity = (proteinCalories / protein.get().getCaloriesPer100g()) * 100;
            meal.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
        }

        // Add vegetables (remaining calories)
        if (vegetables.isPresent() && remainingCalories > 0) {
            double quantity = (remainingCalories / vegetables.get().getCaloriesPer100g()) * 100;
            meal.addFoodItem(new FoodItem(vegetables.get(), Math.max(quantity, 150))); // Min 150g vegetables
        }

        return meal;
    }

    private Meal generateSnack(List<Food> availableFoods, double targetCalories, User user) {
        Meal snack = new Meal("SNACK");

        // Get snack foods or fruits/nuts
        List<Food> snackFoods = availableFoods.stream()
                .filter(f -> "FRUITS".equals(f.getCategory()) ||
                        "NUTS".equals(f.getCategory()) ||
                        "DAIRY".equals(f.getCategory()))
                .collect(Collectors.toList());

        if (!snackFoods.isEmpty()) {
            Food selectedSnack = snackFoods.get((int) (Math.random() * snackFoods.size()));
            double quantity = (targetCalories / selectedSnack.getCaloriesPer100g()) * 100;
            snack.addFoodItem(new FoodItem(selectedSnack, Math.max(quantity, 50))); // Min 50g
        }

        return snack;
    }

    private List<String> getDietTypes(String preference) {
        switch (preference.toUpperCase()) {
            case "VEG":
                return Arrays.asList("VEG", "VEGAN");
            case "NON_VEG":
                return Arrays.asList("VEG", "NON_VEG", "VEGAN");
            case "VEGAN":
                return Arrays.asList("VEGAN");
            default:
                return Arrays.asList("VEG", "NON_VEG", "VEGAN");
        }
    }
}