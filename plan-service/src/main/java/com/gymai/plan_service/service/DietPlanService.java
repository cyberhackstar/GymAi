// Fixed DietPlanService.java
package com.gymai.plan_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gymai.plan_service.entity.DayMealPlan;
import com.gymai.plan_service.entity.DietPlan;
import com.gymai.plan_service.entity.Food;
import com.gymai.plan_service.entity.FoodItem;
import com.gymai.plan_service.entity.Meal;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.repository.DietPlanRepository;
import com.gymai.plan_service.repository.FoodRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class DietPlanService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private DietPlanRepository dietPlanRepository;

    @Autowired
    private NutritionCalculatorService nutritionCalculator;

    public DietPlan generateCustomDietPlan(User user) {
        log.info("Generating custom diet plan for user: {} (preference: {})", user.getUserId(), user.getPreference());

        // Check if user already has a diet plan
        Optional<DietPlan> existingPlan = getExistingDietPlanInternal(user.getUserId());
        if (existingPlan.isPresent()) {
            log.info("Found existing diet plan for userId={}, returning cached plan", user.getUserId());
            return existingPlan.get();
        }

        return generateNewDietPlan(user);
    }

    @Transactional
    public DietPlan regenerateDietPlan(User user) {
        log.info("Regenerating diet plan for userId={}", user.getUserId());

        // Delete existing plan
        dietPlanRepository.deleteByUserId(user.getUserId());

        // Generate new plan
        return generateNewDietPlan(user);
    }

    public DietPlan getExistingDietPlan(Long userId) {
        log.info("Fetching existing diet plan for userId={}", userId);
        return getExistingDietPlanInternal(userId).orElse(null);
    }

    private Optional<DietPlan> getExistingDietPlanInternal(Long userId) {
        Optional<DietPlan> dietPlan = dietPlanRepository.findLatestByUserIdWithDays(userId);
        if (dietPlan.isPresent()) {
            // Manually load the nested relationships to avoid fetch issues
            DietPlan plan = dietPlan.get();
            plan.getDailyPlans().forEach(dayPlan -> {
                dayPlan.getMeals().size(); // Initialize lazy collection
                dayPlan.getMeals().forEach(meal -> {
                    meal.getFoodItems().size(); // Initialize lazy collection
                });
            });
        }
        return dietPlan;
    }

    private DietPlan generateNewDietPlan(User user) {
        // Calculate nutritional needs
        NutritionCalculatorService.NutritionalNeeds needs = nutritionCalculator.calculateNutritionalNeeds(user);
        log.debug("Calculated needs -> Calories: {}, Protein: {}, Carbs: {}, Fat: {}",
                needs.calories, needs.protein, needs.carbs, needs.fat);

        // Create new diet plan
        DietPlan dietPlan = new DietPlan();
        dietPlan.setUserId(user.getUserId());
        dietPlan.setDailyCalorieTarget(needs.calories);
        dietPlan.setDailyProteinTarget(needs.protein);
        dietPlan.setDailyCarbsTarget(needs.carbs);
        dietPlan.setDailyFatTarget(needs.fat);

        // Save the diet plan first to get the ID
        dietPlan = dietPlanRepository.save(dietPlan);

        // Generate 7-day meal plan
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");

        for (int i = 0; i < 7; i++) {
            log.info("Generating meal plan for {}", dayNames.get(i));
            DayMealPlan dayPlan = generateDayMealPlan(i + 1, dayNames.get(i), user, needs);
            dietPlan.addDayMealPlan(dayPlan);
        }

        // Save the complete plan with all relationships
        dietPlan = dietPlanRepository.save(dietPlan);
        log.info("Successfully saved diet plan for userId={} with planId={}", user.getUserId(), dietPlan.getId());

        return dietPlan;
    }

    private DayMealPlan generateDayMealPlan(int dayNumber, String dayName, User user,
            NutritionCalculatorService.NutritionalNeeds needs) {
        log.debug("Generating {} (Day {}) with calorie target: {}", dayName, dayNumber, needs.calories);

        DayMealPlan dayPlan = new DayMealPlan(dayNumber, dayName);

        // Calorie distribution
        double breakfastCalories = needs.calories * 0.25;
        double lunchCalories = needs.calories * 0.35;
        double dinnerCalories = needs.calories * 0.30;
        double snackCalories = needs.calories * 0.10;

        log.debug("Meal distribution -> Breakfast: {}, Lunch: {}, Dinner: {}, Snack: {}",
                breakfastCalories, lunchCalories, dinnerCalories, snackCalories);

        // Generate meals
        dayPlan.addMeal(generateMeal("BREAKFAST", breakfastCalories, user));
        dayPlan.addMeal(generateMeal("LUNCH", lunchCalories, user));
        dayPlan.addMeal(generateMeal("DINNER", dinnerCalories, user));
        dayPlan.addMeal(generateMeal("SNACK", snackCalories, user));

        return dayPlan;
    }

    private Meal generateMeal(String mealType, double targetCalories, User user) {
        log.debug("Generating {} with target calories: {}", mealType, targetCalories);

        Meal meal = new Meal(mealType);
        List<String> dietTypes = getDietTypes(user.getPreference());

        List<Food> availableFoods = foodRepository.findByDietTypeInAndMealType(dietTypes, mealType);
        log.debug("Found {} available foods for {} [{}]", availableFoods.size(), mealType, dietTypes);

        if (availableFoods.isEmpty()) {
            log.warn("No foods found for {}. Falling back to default diet type {} (mealType=LUNCH)", mealType,
                    user.getPreference());
            availableFoods = foodRepository.findByDietTypeAndMealType(user.getPreference().toUpperCase(), "LUNCH");
        }

        switch (mealType.toUpperCase()) {
            case "BREAKFAST":
                return generateBreakfast(availableFoods, targetCalories, user);
            case "LUNCH":
            case "DINNER":
                return generateMainMeal(mealType, availableFoods, targetCalories, user);
            case "SNACK":
                return generateSnack(availableFoods, targetCalories, user);
            default:
                log.error("Unknown meal type: {}", mealType);
                return meal;
        }
    }

    private Meal generateBreakfast(List<Food> availableFoods, double targetCalories, User user) {
        log.debug("Generating Breakfast for user: {}", user.getUserId());

        Meal breakfast = new Meal("BREAKFAST");

        Optional<Food> grains = availableFoods.stream().filter(f -> "GRAINS".equals(f.getCategory())).findFirst();
        Optional<Food> protein = availableFoods.stream()
                .filter(f -> "PROTEIN".equals(f.getCategory()) || "DAIRY".equals(f.getCategory())).findFirst();
        Optional<Food> fruits = availableFoods.stream().filter(f -> "FRUITS".equals(f.getCategory())).findFirst();

        double remainingCalories = targetCalories;

        if (grains.isPresent()) {
            double grainsCalories = targetCalories * 0.4;
            double quantity = (grainsCalories / grains.get().getCaloriesPer100g()) * 100;
            breakfast.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
            log.debug("Added grains: {}g [{}]", quantity, grains.get().getName());
        }

        if (protein.isPresent()) {
            double proteinCalories = targetCalories * 0.35;
            double quantity = (proteinCalories / protein.get().getCaloriesPer100g()) * 100;
            breakfast.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
            log.debug("Added protein: {}g [{}]", quantity, protein.get().getName());
        }

        if (fruits.isPresent() && remainingCalories > 0) {
            double quantity = (remainingCalories / fruits.get().getCaloriesPer100g()) * 100;
            breakfast.addFoodItem(new FoodItem(fruits.get(), Math.max(quantity, 100)));
            log.debug("Added fruit: {}g [{}]", Math.max(quantity, 100), fruits.get().getName());
        }

        return breakfast;
    }

    private Meal generateMainMeal(String mealType, List<Food> availableFoods,
            double targetCalories, User user) {
        log.debug("Generating {} for user: {}", mealType, user.getUserId());

        Meal meal = new Meal(mealType);

        Optional<Food> grains = availableFoods.stream().filter(f -> "GRAINS".equals(f.getCategory()))
                .skip((long) (Math.random()
                        * availableFoods.stream().filter(f -> "GRAINS".equals(f.getCategory())).count()))
                .findFirst();

        Optional<Food> protein = availableFoods.stream().filter(f -> "PROTEIN".equals(f.getCategory()))
                .skip((long) (Math.random()
                        * availableFoods.stream().filter(f -> "PROTEIN".equals(f.getCategory())).count()))
                .findFirst();

        Optional<Food> vegetables = availableFoods.stream().filter(f -> "VEGETABLES".equals(f.getCategory()))
                .skip((long) (Math.random()
                        * availableFoods.stream().filter(f -> "VEGETABLES".equals(f.getCategory())).count()))
                .findFirst();

        double remainingCalories = targetCalories;

        if (grains.isPresent()) {
            double grainsCalories = targetCalories * 0.35;
            double quantity = (grainsCalories / grains.get().getCaloriesPer100g()) * 100;
            meal.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
            log.debug("Added grains: {}g [{}]", quantity, grains.get().getName());
        }

        if (protein.isPresent()) {
            double proteinCalories = targetCalories * 0.45;
            double quantity = (proteinCalories / protein.get().getCaloriesPer100g()) * 100;
            meal.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
            log.debug("Added protein: {}g [{}]", quantity, protein.get().getName());
        }

        if (vegetables.isPresent() && remainingCalories > 0) {
            double quantity = (remainingCalories / vegetables.get().getCaloriesPer100g()) * 100;
            meal.addFoodItem(new FoodItem(vegetables.get(), Math.max(quantity, 150)));
            log.debug("Added vegetables: {}g [{}]", Math.max(quantity, 150), vegetables.get().getName());
        }

        return meal;
    }

    private Meal generateSnack(List<Food> availableFoods, double targetCalories, User user) {
        log.debug("Generating Snack for user: {}", user.getUserId());

        Meal snack = new Meal("SNACK");

        List<Food> snackFoods = availableFoods.stream()
                .filter(f -> "FRUITS".equals(f.getCategory()) ||
                        "NUTS".equals(f.getCategory()) ||
                        "DAIRY".equals(f.getCategory()))
                .collect(Collectors.toList());

        if (!snackFoods.isEmpty()) {
            Food selectedSnack = snackFoods.get((int) (Math.random() * snackFoods.size()));
            double quantity = (targetCalories / selectedSnack.getCaloriesPer100g()) * 100;
            snack.addFoodItem(new FoodItem(selectedSnack, Math.max(quantity, 50)));
            log.debug("Added snack: {}g [{}]", Math.max(quantity, 50), selectedSnack.getName());
        } else {
            log.warn("No snack foods available for user: {}", user.getUserId());
        }

        return snack;
    }

    private List<String> getDietTypes(String preference) {
        log.debug("Resolving diet types for preference: {}", preference);

        switch (preference.toUpperCase()) {
            case "VEG":
                return Arrays.asList("VEG", "VEGAN");
            case "NON_VEG":
                return Arrays.asList("VEG", "NON_VEG", "VEGAN");
            case "VEGAN":
                return Arrays.asList("VEGAN");
            default:
                log.warn("Unknown diet preference: {}. Defaulting to all types.", preference);
                return Arrays.asList("VEG", "NON_VEG", "VEGAN");
        }
    }
}