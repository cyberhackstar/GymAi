package com.gymai.plan_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.entity.*;
import com.gymai.plan_service.repository.*;
import com.gymai.plan_service.dto.SimpleDietPlanDTO;
import com.gymai.plan_service.mapper.DietPlanMapper;
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

    @Autowired
    private CacheService cacheService;

    @Autowired
    private DietPlanMapper dietPlanMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public DietPlan generateCustomDietPlan(User user) {
        log.info("Generating custom diet plan for user: {} (preference: {})", user.getUserId(), user.getPreference());

        // Check cache first
        SimpleDietPlanDTO cachedPlan = cacheService.getCachedDietPlan(user.getUserId());
        if (cachedPlan != null) {
            log.info("Found cached diet plan for userId={}, returning cached plan", user.getUserId());
            DietPlan existingPlan = getExistingDietPlanSafe(user.getUserId());
            if (existingPlan != null) {
                return existingPlan;
            }
        }

        // Check if user already has a diet plan using safe method
        DietPlan existingPlan = getExistingDietPlanSafe(user.getUserId());
        if (existingPlan != null) {
            log.info("Found existing diet plan for userId={}, caching and returning plan", user.getUserId());
            SimpleDietPlanDTO planDTO = dietPlanMapper.toDTO(existingPlan);
            cacheService.cacheDietPlan(user.getUserId(), planDTO);
            return existingPlan;
        }

        return generateNewDietPlan(user);
    }

    @Transactional
    @CacheEvict(value = "diet-plans", key = "#user.userId")
    public DietPlan regenerateDietPlan(User user) {
        log.info("Regenerating diet plan for userId={}", user.getUserId());

        // Clear cache
        cacheService.invalidateUserPlansCache(user.getUserId());

        // Delete existing plan
        // cacheService.invalidateUserPlansCache(userId);
        List<DietPlan> plans = dietPlanRepository.findByUserId(user.getUserId());
        for (DietPlan plan : plans) {
            dietPlanRepository.delete(plan); // This triggers cascade and orphan removal.
        }
        log.info("Deleted {} diet plans for userId={}", plans.size(), user.getUserId());
        entityManager.flush(); // Ensure deletion is committed

        // Generate new plan
        return generateNewDietPlan(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "diet-plans", key = "#userId")
    public DietPlan getExistingDietPlan(Long userId) {
        log.info("Fetching existing diet plan for userId={}", userId);

        // Check cache first
        SimpleDietPlanDTO cachedPlan = cacheService.getCachedDietPlan(userId);
        if (cachedPlan != null) {
            log.debug("Retrieved diet plan from cache for userId={}", userId);
        }

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
    @CacheEvict(value = "diet-plans", key = "#userId")
    public void deleteUserDietPlans(Long userId) {
        log.info("Deleting diet plans for userId: {}", userId);
        cacheService.invalidateUserPlansCache(userId);
        List<DietPlan> plans = dietPlanRepository.findByUserId(userId);
        for (DietPlan plan : plans) {
            dietPlanRepository.delete(plan); // This triggers cascade and orphan removal.
        }
        log.info("Deleted {} diet plans for userId={}", plans.size(), userId);
    }

    @Transactional
    private DietPlan generateNewDietPlan(User user) {
        // Calculate nutritional needs
        NutritionCalculatorService.NutritionalNeeds needs = nutritionCalculator.calculateNutritionalNeeds(user);

        // Round off nutrition values to 2 decimal places
        needs.calories = Math.round(needs.calories * 100.0) / 100.0;

        needs.protein = Math.round(needs.protein * 100.0) / 100.0;

        needs.carbs = Math.round(needs.carbs * 100.0) / 100.0;
        needs.fat = Math.round(needs.fat * 100.0) / 100.0;

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

        // Pre-fetch foods for better performance (with caching)
        Map<String, List<Food>> foodsByMealTypeAndCategory = preloadFoodsByUserPreference(user);

        // Validate that we have foods available
        if (foodsByMealTypeAndCategory.isEmpty()
                || foodsByMealTypeAndCategory.values().stream().allMatch(List::isEmpty)) {
            log.error("No foods available for user preference: {}. Cannot generate diet plan.", user.getPreference());
            throw new RuntimeException("No foods available for the selected dietary preference");
        }

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

        // Cache the result
        SimpleDietPlanDTO planDTO = dietPlanMapper.toDTO(dietPlan);
        cacheService.cacheDietPlan(user.getUserId(), planDTO);

        log.info("Successfully saved and cached diet plan for userId={} with planId={}", user.getUserId(),
                dietPlan.getId());

        return dietPlan;
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<Food>> preloadFoodsByUserPreference(User user) {
        String cacheKey = user.getPreference();

        // Check cache first
        Object cachedFoods = cacheService.getCachedFoodsByPreference(cacheKey);
        if (cachedFoods instanceof Map) {
            log.debug("Retrieved foods from cache for preference: {}", user.getPreference());
            return (Map<String, List<Food>>) cachedFoods;
        }

        Map<String, List<Food>> foodMap = new HashMap<>();
        List<String> dietTypes = getDietTypes(user.getPreference());
        List<String> mealTypes = Arrays.asList("BREAKFAST", "LUNCH", "DINNER", "SNACK");

        log.debug("Preloading foods for diet types: {} and meal types: {}", dietTypes, mealTypes);

        for (String mealType : mealTypes) {
            List<Food> foods = foodRepository.findByDietTypeInAndMealType(dietTypes, mealType);

            // Remove any invalid foods (null values or foods with zero/negative nutrition)
            foods = foods.stream()
                    .filter(food -> food != null &&
                            food.getCaloriesPer100g() > 0 &&
                            food.getName() != null && !food.getName().trim().isEmpty())
                    .collect(Collectors.toList());

            foodMap.put(mealType, foods);

            // If no foods found for specific meal type, use lunch as fallback
            if (foods.isEmpty() && !mealType.equals("LUNCH")) {
                List<Food> lunchFoods = foodRepository.findByDietTypeInAndMealType(dietTypes, "LUNCH");
                lunchFoods = lunchFoods.stream()
                        .filter(food -> food != null &&
                                food.getCaloriesPer100g() > 0 &&
                                food.getName() != null && !food.getName().trim().isEmpty())
                        .collect(Collectors.toList());
                foodMap.put(mealType, lunchFoods);
                log.warn("No foods found for meal type {}. Using LUNCH foods as fallback.", mealType);
            }
        }

        // Cache the result
        cacheService.cacheFoodsByPreference(cacheKey, foodMap);
        log.debug("Cached foods for preference: {}", user.getPreference());

        return foodMap;
    }

    private DayMealPlan generateDayMealPlan(int dayNumber, String dayName, User user,
            NutritionCalculatorService.NutritionalNeeds needs, Map<String, List<Food>> preloadedFoods) {
        log.debug("Generating {} (Day {}) with calorie target: {}", dayName, dayNumber, needs.calories);

        DayMealPlan dayPlan = new DayMealPlan(dayNumber, dayName);

        // Calorie distribution - rounded to 2 decimal places
        double breakfastCalories = Math.round(needs.calories * 0.25 * 100.0) / 100.0;
        double lunchCalories = Math.round(needs.calories * 0.35 * 100.0) / 100.0;
        double dinnerCalories = Math.round(needs.calories * 0.30 * 100.0) / 100.0;
        double snackCalories = Math.round(needs.calories * 0.10 * 100.0) / 100.0;

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

        Optional<Food> grains = getRandomFoodByCategory(availableFoods, "GRAINS");
        Optional<Food> protein = availableFoods.stream()
                .filter(f -> "PROTEIN".equals(f.getCategory()) || "DAIRY".equals(f.getCategory()))
                .findFirst();
        Optional<Food> fruits = getRandomFoodByCategory(availableFoods, "FRUITS");

        double remainingCalories = targetCalories;

        if (grains.isPresent()) {
            double grainsCalories = Math.round(targetCalories * 0.4 * 100.0) / 100.0;
            double quantity = Math.max(50,
                    Math.round(((grainsCalories / grains.get().getCaloriesPer100g()) * 100) * 100.0) / 100.0);
            breakfast.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
            log.debug("Added grains: {}g [{}] - {} calories", quantity, grains.get().getName(), grainsCalories);
        }

        if (protein.isPresent()) {
            double proteinCalories = Math.round(targetCalories * 0.35 * 100.0) / 100.0;
            double quantity = Math.max(30,
                    Math.round(((proteinCalories / protein.get().getCaloriesPer100g()) * 100) * 100.0) / 100.0);
            breakfast.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
            log.debug("Added protein: {}g [{}] - {} calories", quantity, protein.get().getName(), proteinCalories);
        }

        if (fruits.isPresent() && remainingCalories > 0) {
            double quantity = Math.max(100,
                    Math.round(((remainingCalories / fruits.get().getCaloriesPer100g()) * 100) * 100.0) / 100.0);
            breakfast.addFoodItem(new FoodItem(fruits.get(), quantity));
            log.debug("Added fruit: {}g [{}] - {} calories", quantity, fruits.get().getName(), remainingCalories);
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
            double grainsCalories = Math.round(targetCalories * 0.35 * 100.0) / 100.0;
            double quantity = Math.max(80,
                    Math.round(((grainsCalories / grains.get().getCaloriesPer100g()) * 100) * 100.0) / 100.0);
            meal.addFoodItem(new FoodItem(grains.get(), quantity));
            remainingCalories -= grainsCalories;
            log.debug("Added grains: {}g [{}] - {} calories", quantity, grains.get().getName(), grainsCalories);
        }

        if (protein.isPresent()) {
            double proteinCalories = Math.round(targetCalories * 0.45 * 100.0) / 100.0;
            double quantity = Math.max(100,
                    Math.round(((proteinCalories / protein.get().getCaloriesPer100g()) * 100) * 100.0) / 100.0);
            meal.addFoodItem(new FoodItem(protein.get(), quantity));
            remainingCalories -= proteinCalories;
            log.debug("Added protein: {}g [{}] - {} calories", quantity, protein.get().getName(), proteinCalories);
        }

        if (vegetables.isPresent() && remainingCalories > 0) {
            double quantity = Math.max(150,
                    Math.round(((remainingCalories / vegetables.get().getCaloriesPer100g()) * 100) * 100.0) / 100.0);
            meal.addFoodItem(new FoodItem(vegetables.get(), quantity));
            log.debug("Added vegetables: {}g [{}] - {} calories", quantity, vegetables.get().getName(),
                    remainingCalories);
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
            double quantity = Math.max(50,
                    Math.round(((targetCalories / selectedSnack.getCaloriesPer100g()) * 100) * 10.0) / 10.0);
            snack.addFoodItem(new FoodItem(selectedSnack, quantity));
            log.debug("Added snack: {}g [{}] - {} calories", quantity, selectedSnack.getName(), targetCalories);
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

        if (preference == null) {
            log.warn("Null diet preference. Defaulting to vegetarian.");
            return Arrays.asList("VEG", "VEGAN");
        }

        switch (preference.toUpperCase().trim()) {
            case "VEG":
            case "VEGETARIAN":
                return Arrays.asList("VEG", "VEGAN");
            case "NON_VEG":
            case "NON-VEG":
            case "NONVEG":
            case "OMNIVORE":
                return Arrays.asList("VEG", "NON_VEG", "VEGAN");
            case "VEGAN":
                return Arrays.asList("VEGAN");
            default:
                log.warn("Unknown diet preference: {}. Defaulting to vegetarian.", preference);
                return Arrays.asList("VEG", "VEGAN");
        }
    }
}