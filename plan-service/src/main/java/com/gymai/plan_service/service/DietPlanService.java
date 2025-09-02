package com.gymai.plan_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.entity.*;
import com.gymai.plan_service.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DietPlanService {

    private static final Logger log = LoggerFactory.getLogger(DietPlanService.class);

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private DietPlanRepository dietPlanRepository;

    @Autowired
    private NutritionCalculatorService nutritionCalculator;

    @PersistenceContext
    private EntityManager entityManager;

    public DietPlan generateCustomDietPlan(User user) {
        log.info("Generating custom diet plan for user: {} (preference: {})", user.getUserId(), user.getPreference());

        // Check if user already has a diet plan using safe method
        DietPlan existingPlan = getExistingDietPlanSafe(user.getUserId());
        if (existingPlan != null) {
            log.info("Found existing diet plan for userId={}, returning cached plan", user.getUserId());
            return existingPlan;
        }

        return generateNewDietPlan(user);
    }

    @Transactional
    public DietPlan regenerateDietPlan(User user) {
        log.info("Regenerating diet plan for userId={}", user.getUserId());

        // Delete existing plan
        dietPlanRepository.deleteByUserId(user.getUserId());
        entityManager.flush(); // Ensure deletion is committed

        // Generate new plan
        return generateNewDietPlan(user);
    }

    @Transactional(readOnly = true)
    public DietPlan getExistingDietPlan(Long userId) {
        log.info("Fetching existing diet plan for userId={}", userId);
        return getExistingDietPlanSafe(userId);
    }

    @Transactional(readOnly = true)
    private DietPlan getExistingDietPlanSafe(Long userId) {
        // Step 1: Get basic diet plan
        Optional<DietPlan> planOpt = dietPlanRepository.findLatestByUserId(userId);
        if (!planOpt.isPresent()) {
            return null;
        }

        DietPlan dietPlan = planOpt.get();

        // Step 2: Manually load daily plans
        List<DayMealPlan> dayMealPlans = entityManager.createQuery(
                "SELECT dmp FROM DayMealPlan dmp WHERE dmp.dietPlan.id = :planId ORDER BY dmp.dayNumber",
                DayMealPlan.class)
                .setParameter("planId", dietPlan.getId())
                .getResultList();

        // Step 3: Load meals for each day plan
        for (DayMealPlan dayPlan : dayMealPlans) {
            List<Meal> meals = entityManager.createQuery(
                    "SELECT m FROM Meal m WHERE m.dayMealPlan.id = :dayPlanId ORDER BY m.mealType",
                    Meal.class)
                    .setParameter("dayPlanId", dayPlan.getId())
                    .getResultList();

            // Step 4: Load food items for each meal
            for (Meal meal : meals) {
                List<FoodItem> foodItems = entityManager.createQuery(
                        "SELECT fi FROM FoodItem fi JOIN FETCH fi.food WHERE fi.meal.id = :mealId",
                        FoodItem.class)
                        .setParameter("mealId", meal.getId())
                        .getResultList();

                meal.getFoodItems().clear();
                meal.getFoodItems().addAll(foodItems);

                // Set back reference
                for (FoodItem item : foodItems) {
                    item.setMeal(meal);
                }
            }

            dayPlan.getMeals().clear();
            dayPlan.getMeals().addAll(meals);

            // Set back references
            for (Meal meal : meals) {
                meal.setDayMealPlan(dayPlan);
            }
        }

        dietPlan.getDailyPlans().clear();
        dietPlan.getDailyPlans().addAll(dayMealPlans);

        // Set back references
        for (DayMealPlan dayPlan : dayMealPlans) {
            dayPlan.setDietPlan(dietPlan);
        }

        log.debug("Successfully loaded diet plan with {} daily plans", dayMealPlans.size());
        return dietPlan;
    }

    @Transactional
    public void deleteUserPlans(Long userId) {
        log.info("Deleting diet plans for userId: {}", userId);
        List<DietPlan> plans = dietPlanRepository.findByUserId(userId);
        if (!plans.isEmpty()) {
            dietPlanRepository.deleteAll(plans);
            log.info("Deleted {} diet plans for userId: {}", plans.size(), userId);
        }
    }

    @Transactional
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

        // Pre-fetch foods for better performance
        Map<String, List<Food>> foodsByMealTypeAndCategory = preloadFoodsByUserPreference(user);

        // Generate 7-day meal plan
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");

        for (int i = 0; i < 7; i++) {
            log.debug("Generating meal plan for {}", dayNames.get(i));
            DayMealPlan dayPlan = generateDayMealPlan(i + 1, dayNames.get(i), user, needs, foodsByMealTypeAndCategory);
            dietPlan.addDayMealPlan(dayPlan);
        }

        // Save the complete plan with all relationships
        dietPlan = dietPlanRepository.save(dietPlan);
        log.info("Successfully saved diet plan for userId={} with planId={}", user.getUserId(), dietPlan.getId());

        return dietPlan;
    }

    private Map<String, List<Food>> preloadFoodsByUserPreference(User user) {
        Map<String, List<Food>> foodMap = new HashMap<>();
        List<String> dietTypes = getDietTypes(user.getPreference());
        List<String> mealTypes = Arrays.asList("BREAKFAST", "LUNCH", "DINNER", "SNACK");

        log.debug("Preloading foods for diet types: {} and meal types: {}", dietTypes, mealTypes);

        for (String mealType : mealTypes) {
            List<Food> foods = foodRepository.findByDietTypeInAndMealType(dietTypes, mealType);
            foodMap.put(mealType, foods);

            // If no foods found for specific meal type, use lunch as fallback
            if (foods.isEmpty() && !mealType.equals("LUNCH")) {
                foods = foodRepository.findByDietTypeInAndMealType(dietTypes, "LUNCH");
                foodMap.put(mealType, foods);
                log.warn("No foods found for meal type {}. Using LUNCH foods as fallback.", mealType);
            }
        }

        return foodMap;
    }

    private DayMealPlan generateDayMealPlan(int dayNumber, String dayName, User user,
            NutritionCalculatorService.NutritionalNeeds needs, Map<String, List<Food>> preloadedFoods) {
        log.debug("Generating {} (Day {}) with calorie target: {}", dayName, dayNumber, needs.calories);

        DayMealPlan dayPlan = new DayMealPlan(dayNumber, dayName);

        // Calorie distribution
        double breakfastCalories = needs.calories * 0.25;
        double lunchCalories = needs.calories * 0.35;
        double dinnerCalories = needs.calories * 0.30;
        double snackCalories = needs.calories * 0.10;

        log.debug("Meal distribution -> Breakfast: {}, Lunch: {}, Dinner: {}, Snack: {}",
                breakfastCalories, lunchCalories, dinnerCalories, snackCalories);

        // Generate meals using preloaded foods
        dayPlan.addMeal(generateMeal("BREAKFAST", breakfastCalories, user, preloadedFoods));
        dayPlan.addMeal(generateMeal("LUNCH", lunchCalories, user, preloadedFoods));
        dayPlan.addMeal(generateMeal("DINNER", dinnerCalories, user, preloadedFoods));
        dayPlan.addMeal(generateMeal("SNACK", snackCalories, user, preloadedFoods));

        return dayPlan;
    }

    private Meal generateMeal(String mealType, double targetCalories, User user,
            Map<String, List<Food>> preloadedFoods) {
        log.debug("Generating {} with target calories: {}", mealType, targetCalories);

        List<Food> availableFoods = preloadedFoods.get(mealType);
        if (availableFoods == null || availableFoods.isEmpty()) {
            log.warn("No available foods for meal type: {}. Using fallback.", mealType);
            availableFoods = preloadedFoods.get("LUNCH");
            if (availableFoods == null || availableFoods.isEmpty()) {
                log.error("No foods available even with fallback for user: {}", user.getUserId());
                return new Meal(mealType); // Return empty meal
            }
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
                return new Meal(mealType);
        }
    }

    private Meal generateBreakfast(List<Food> availableFoods, double targetCalories, User user) {
        log.debug("Generating Breakfast for user: {}", user.getUserId());

        Meal breakfast = new Meal("BREAKFAST");

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

        if (grains.isPresent()) {
            double grainsCalories = targetCalories * 0.4;
            double quantity = Math.max(50, (grainsCalories / grains.get().getCaloriesPer100g()) * 100);
            breakfast.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
            log.debug("Added grains: {}g [{}]", quantity, grains.get().getName());
        }

        if (protein.isPresent()) {
            double proteinCalories = targetCalories * 0.35;
            double quantity = Math.max(30, (proteinCalories / protein.get().getCaloriesPer100g()) * 100);
            breakfast.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
            log.debug("Added protein: {}g [{}]", quantity, protein.get().getName());
        }

        if (fruits.isPresent() && remainingCalories > 0) {
            double quantity = Math.max(100, (remainingCalories / fruits.get().getCaloriesPer100g()) * 100);
            breakfast.addFoodItem(new FoodItem(fruits.get(), quantity));
            log.debug("Added fruit: {}g [{}]", quantity, fruits.get().getName());
        }

        return breakfast;
    }

    private Meal generateMainMeal(String mealType, List<Food> availableFoods,
            double targetCalories, User user) {
        log.debug("Generating {} for user: {}", mealType, user.getUserId());

        Meal meal = new Meal(mealType);

        // Use random selection for variety
        Optional<Food> grains = getRandomFoodByCategory(availableFoods, "GRAINS");
        Optional<Food> protein = getRandomFoodByCategory(availableFoods, "PROTEIN");
        Optional<Food> vegetables = getRandomFoodByCategory(availableFoods, "VEGETABLES");

        double remainingCalories = targetCalories;

        if (grains.isPresent()) {
            double grainsCalories = targetCalories * 0.35;
            double quantity = Math.max(80, (grainsCalories / grains.get().getCaloriesPer100g()) * 100);
            meal.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
            log.debug("Added grains: {}g [{}]", quantity, grains.get().getName());
        }

        if (protein.isPresent()) {
            double proteinCalories = targetCalories * 0.45;
            double quantity = Math.max(100, (proteinCalories / protein.get().getCaloriesPer100g()) * 100);
            meal.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
            log.debug("Added protein: {}g [{}]", quantity, protein.get().getName());
        }

        if (vegetables.isPresent() && remainingCalories > 0) {
            double quantity = Math.max(150, (remainingCalories / vegetables.get().getCaloriesPer100g()) * 100);
            meal.addFoodItem(new FoodItem(vegetables.get(), quantity));
            log.debug("Added vegetables: {}g [{}]", quantity, vegetables.get().getName());
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
            double quantity = Math.max(50, (targetCalories / selectedSnack.getCaloriesPer100g()) * 100);
            snack.addFoodItem(new FoodItem(selectedSnack, quantity));
            log.debug("Added snack: {}g [{}]", quantity, selectedSnack.getName());
        } else {
            log.warn("No snack foods available for user: {}", user.getUserId());
        }

        return snack;
    }

    private Optional<Food> getRandomFoodByCategory(List<Food> foods, String category) {
        List<Food> categoryFoods = foods.stream()
                .filter(f -> category.equals(f.getCategory()))
                .collect(Collectors.toList());

        if (categoryFoods.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(categoryFoods.get((int) (Math.random() * categoryFoods.size())));
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