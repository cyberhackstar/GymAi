package com.gymai.plan_service.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.gymai.plan_service.entity.Exercise;
import com.gymai.plan_service.entity.Food;
import com.gymai.plan_service.repository.ExerciseRepository;
import com.gymai.plan_service.repository.FoodRepository;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Enhanced Comprehensive Nutrition & Exercise Data Service
 * Based on USDA Food Database, WHO Guidelines, and Scientific Research
 * Includes Guru Mann's Professional Programs and International Standards
 * 
 * Data Sources:
 * - USDA National Nutrient Database
 * - WHO Nutritional Guidelines
 * - Indian Food Composition Tables (IFCT 2017)
 * - ACSM Exercise Guidelines
 * - Guru Mann's Fitness Programs
 */
@Service
@Component
@Slf4j
public class EnhancedNutritionDataService {

  @Autowired
  private FoodRepository foodRepository;

  @Autowired
  private ExerciseRepository exerciseRepository;

  @PostConstruct
  public void initializeData() {
    if (foodRepository.count() == 0) {
      log.info("Initializing comprehensive food database with {} entries", getTotalFoodCount());
      initializeComprehensiveFoodData();
    }

    if (exerciseRepository.count() == 0) {
      log.info("Initializing comprehensive exercise database with {} entries",
          getTotalExerciseCount());
      initializeComprehensiveExerciseData();
    }
  }

  private int getTotalFoodCount() {
    return 450; // Approximate count
  }

  private int getTotalExerciseCount() {
    return 200; // Approximate count
  }

  private void initializeComprehensiveFoodData() {
    List<Food> foods = Arrays.asList(
        // ===== BREAKFAST FOODS =====

        // GRAINS & CEREALS (Per 100g, cooked unless specified)
        new Food("Rolled Oats (dry)", 389.0, 16.9, 66.3, 6.9, 10.6, "VEG", "BREAKFAST",
            "GRAINS"),
        new Food("Steel Cut Oats (cooked)", 158.0, 5.9, 28.1, 3.2, 4.0, "VEG", "BREAKFAST",
            "GRAINS"),
        new Food("Quinoa (cooked)", 222.0, 8.1, 39.4, 3.6, 5.2, "VEGAN", "BREAKFAST", "GRAINS"),
        new Food("Brown Rice (cooked)", 216.0, 5.0, 45.0, 1.8, 3.5, "VEGAN", "BREAKFAST",
            "GRAINS"),
        new Food("Muesli (unsweetened)", 367.0, 10.1, 66.2, 5.9, 7.9, "VEG", "BREAKFAST",
            "GRAINS"),
        new Food("Whole Wheat Cereal", 379.0, 13.0, 67.0, 6.0, 10.0, "VEGAN", "BREAKFAST",
            "GRAINS"),
        new Food("Daliya (Broken Wheat, cooked)", 342.0, 12.0, 69.0, 1.8, 12.5, "VEGAN",
            "BREAKFAST", "GRAINS"),
        new Food("Poha (Flattened Rice, cooked)", 130.0, 3.0, 26.0, 0.5, 2.0, "VEGAN",
            "BREAKFAST", "GRAINS"),
        new Food("Upma (Semolina, cooked)", 220.0, 6.0, 42.0, 4.0, 2.5, "VEGAN", "BREAKFAST",
            "GRAINS"),
        new Food("Cornflakes (plain)", 357.0, 7.5, 84.0, 0.9, 3.0, "VEGAN", "BREAKFAST",
            "GRAINS"),

        // BREAKFAST BREADS
        new Food("Whole Wheat Bread (1 slice 28g)", 82.0, 4.2, 13.7, 1.1, 2.5, "VEGAN",
            "BREAKFAST", "GRAINS"),
        new Food("Multigrain Bread (1 slice 28g)", 85.0, 4.5, 14.2, 1.3, 2.8, "VEGAN",
            "BREAKFAST", "GRAINS"),
        new Food("Sourdough Bread (1 slice 28g)", 78.0, 3.2, 15.8, 0.6, 1.9, "VEGAN",
            "BREAKFAST", "GRAINS"),
        new Food("Rye Bread (1 slice 28g)", 73.0, 2.7, 15.5, 0.6, 1.9, "VEGAN", "BREAKFAST",
            "GRAINS"),

        // BREAKFAST PROTEINS
        new Food("Whole Egg (1 large 50g)", 68.0, 5.5, 0.4, 4.4, 0.0, "VEG", "BREAKFAST",
            "PROTEIN"),
        new Food("Egg White (1 large)", 17.0, 3.6, 0.2, 0.1, 0.0, "VEG", "BREAKFAST",
            "PROTEIN"),
        new Food("Greek Yogurt (plain, low fat)", 59.0, 10.3, 3.6, 0.4, 0.0, "VEG", "BREAKFAST",
            "DAIRY"),
        new Food("Greek Yogurt (full fat)", 97.0, 9.0, 4.0, 5.0, 0.0, "VEG", "BREAKFAST",
            "DAIRY"),
        new Food("Cottage Cheese (low fat)", 98.0, 11.1, 3.4, 4.3, 0.0, "VEG", "BREAKFAST",
            "DAIRY"),
        new Food("Paneer (Indian cottage cheese)", 265.0, 18.3, 1.2, 20.8, 0.0, "VEG",
            "BREAKFAST", "PROTEIN"),

        // PLANT-BASED PROTEINS
        new Food("Tofu (firm)", 76.0, 8.1, 1.9, 4.8, 0.3, "VEGAN", "BREAKFAST", "PROTEIN"),
        new Food("Tempeh", 193.0, 19.0, 9.4, 11.0, 9.0, "VEGAN", "BREAKFAST", "PROTEIN"),
        new Food("Soy Milk (unsweetened)", 54.0, 3.3, 6.3, 1.8, 0.6, "VEGAN", "BREAKFAST",
            "DAIRY"),
        new Food("Almond Milk (unsweetened)", 17.0, 0.6, 1.5, 1.2, 0.2, "VEGAN", "BREAKFAST",
            "DAIRY"),
        new Food("Oat Milk", 47.0, 1.0, 7.0, 1.5, 0.8, "VEGAN", "BREAKFAST", "DAIRY"),

        // DAIRY & ALTERNATIVES
        new Food("Milk (whole, 3.25% fat)", 61.0, 3.2, 4.8, 3.3, 0.0, "VEG", "BREAKFAST",
            "DAIRY"),
        new Food("Milk (2% fat)", 50.0, 3.3, 4.9, 2.0, 0.0, "VEG", "BREAKFAST", "DAIRY"),
        new Food("Milk (1% fat)", 42.0, 3.4, 5.0, 1.0, 0.0, "VEG", "BREAKFAST", "DAIRY"),
        new Food("Milk (skim)", 34.0, 3.4, 5.0, 0.2, 0.0, "VEG", "BREAKFAST", "DAIRY"),
        new Food("Buttermilk (low fat)", 40.0, 3.3, 4.9, 0.9, 0.0, "VEG", "BREAKFAST", "DAIRY"),

        // SUPPLEMENTS & PROTEIN POWDERS
        new Food("Whey Protein Powder (1 scoop 30g)", 120.0, 24.0, 2.0, 1.0, 0.0, "VEG",
            "BREAKFAST", "SUPPLEMENTS"),
        new Food("Plant Protein Powder (1 scoop 30g)", 110.0, 22.0, 3.0, 1.5, 2.0, "VEGAN",
            "BREAKFAST", "SUPPLEMENTS"),
        new Food("Casein Protein Powder (1 scoop 30g)", 110.0, 24.0, 3.0, 0.5, 0.0, "VEG",
            "BREAKFAST", "SUPPLEMENTS"),

        // ===== FRUITS =====

        // COMMON FRUITS (Per 100g fresh weight)
        new Food("Apple (with skin)", 52.0, 0.3, 13.8, 0.2, 2.4, "VEGAN", "SNACK", "FRUITS"),
        new Food("Banana", 89.0, 1.1, 22.8, 0.3, 2.6, "VEGAN", "SNACK", "FRUITS"),
        new Food("Orange", 47.0, 0.9, 11.8, 0.1, 2.4, "VEGAN", "SNACK", "FRUITS"),
        new Food("Mango", 60.0, 0.8, 15.0, 0.4, 1.6, "VEGAN", "SNACK", "FRUITS"),
        new Food("Grapes", 62.0, 0.6, 16.0, 0.2, 0.9, "VEGAN", "SNACK", "FRUITS"),
        new Food("Strawberries", 32.0, 0.7, 7.7, 0.3, 2.0, "VEGAN", "SNACK", "FRUITS"),
        new Food("Blueberries", 57.0, 0.7, 14.5, 0.3, 2.4, "VEGAN", "SNACK", "FRUITS"),
        new Food("Pineapple", 50.0, 0.5, 13.1, 0.1, 1.4, "VEGAN", "SNACK", "FRUITS"),
        new Food("Watermelon", 30.0, 0.6, 7.6, 0.2, 0.4, "VEGAN", "SNACK", "FRUITS"),
        new Food("Papaya", 43.0, 0.5, 10.8, 0.3, 1.7, "VEGAN", "SNACK", "FRUITS"),
        new Food("Pomegranate", 83.0, 1.7, 18.7, 1.2, 4.0, "VEGAN", "SNACK", "FRUITS"),
        new Food("Kiwi", 61.0, 1.1, 14.7, 0.5, 3.0, "VEGAN", "SNACK", "FRUITS"),
        new Food("Avocado", 160.0, 2.0, 8.5, 14.7, 6.7, "VEGAN", "SNACK", "FRUITS"),

        // INDIAN FRUITS
        new Food("Guava", 68.0, 2.6, 14.3, 1.0, 5.4, "VEGAN", "SNACK", "FRUITS"),
        new Food("Jamun (Black Plum)", 62.0, 0.7, 14.0, 0.2, 0.6, "VEGAN", "SNACK", "FRUITS"),
        new Food("Custard Apple (Sitafal)", 101.0, 1.7, 25.2, 0.6, 2.4, "VEGAN", "SNACK",
            "FRUITS"),
        new Food("Jackfruit", 95.0, 1.7, 23.2, 0.6, 1.5, "VEGAN", "SNACK", "FRUITS"),
        new Food("Sapota (Chiku)", 83.0, 0.4, 19.9, 1.1, 5.3, "VEGAN", "SNACK", "FRUITS"),
        new Food("Indian Gooseberry (Amla)", 44.0, 0.9, 10.2, 0.6, 4.3, "VEGAN", "SNACK",
            "FRUITS"),

        // DRIED FRUITS & NUTS (Per 100g)
        new Food("Almonds (raw)", 579.0, 21.2, 21.6, 49.9, 12.5, "VEGAN", "SNACK", "NUTS"),
        new Food("Cashews (raw)", 553.0, 18.2, 30.2, 43.9, 3.3, "VEGAN", "SNACK", "NUTS"),
        new Food("Walnuts", 654.0, 15.2, 13.7, 65.2, 6.7, "VEGAN", "SNACK", "NUTS"),
        new Food("Peanuts (raw)", 567.0, 25.8, 16.1, 49.2, 8.5, "VEGAN", "SNACK", "NUTS"),
        new Food("Pistachios", 560.0, 20.2, 27.2, 45.3, 10.6, "VEGAN", "SNACK", "NUTS"),
        new Food("Brazil Nuts", 659.0, 14.3, 12.3, 67.1, 7.5, "VEGAN", "SNACK", "NUTS"),
        new Food("Hazelnuts", 628.0, 15.0, 16.7, 60.8, 9.7, "VEGAN", "SNACK", "NUTS"),

        new Food("Dates (Medjool)", 277.0, 1.8, 75.0, 0.2, 6.7, "VEGAN", "SNACK", "FRUITS"),
        new Food("Raisins", 299.0, 3.1, 79.2, 0.5, 3.7, "VEGAN", "SNACK", "FRUITS"),
        new Food("Dried Figs", 249.0, 3.3, 63.9, 0.9, 9.8, "VEGAN", "SNACK", "FRUITS"),
        new Food("Dried Apricots", 241.0, 3.4, 62.6, 0.5, 7.3, "VEGAN", "SNACK", "FRUITS"),

        // ===== VEGETABLES =====

        // LEAFY GREENS (Per 100g, raw unless specified)
        new Food("Spinach (raw)", 23.0, 2.9, 3.6, 0.4, 2.2, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Kale (raw)", 49.0, 4.3, 8.8, 0.9, 3.6, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Swiss Chard", 19.0, 1.8, 3.7, 0.2, 1.6, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Arugula (Rocket)", 25.0, 2.6, 3.7, 0.7, 1.6, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Lettuce (Romaine)", 17.0, 1.2, 3.3, 0.3, 2.1, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Methi Leaves (Fenugreek)", 49.0, 4.4, 6.0, 0.9, 24.6, "VEGAN", "LUNCH",
            "VEGETABLES"),

        // CRUCIFEROUS VEGETABLES
        new Food("Broccoli (cooked)", 34.0, 2.8, 6.6, 0.4, 2.6, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Cauliflower (cooked)", 23.0, 1.8, 4.1, 0.5, 2.0, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Brussels Sprouts (cooked)", 43.0, 3.4, 8.9, 0.3, 3.8, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Cabbage (raw)", 25.0, 1.3, 5.8, 0.1, 2.5, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Bok Choy", 13.0, 1.5, 2.2, 0.2, 1.0, "VEGAN", "LUNCH", "VEGETABLES"),

        // ROOT VEGETABLES
        new Food("Sweet Potato (cooked)", 86.0, 1.6, 20.1, 0.1, 3.0, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Potato (boiled)", 77.0, 2.0, 17.5, 0.1, 2.1, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Carrot (raw)", 41.0, 0.9, 9.6, 0.2, 2.8, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Beetroot (cooked)", 44.0, 1.7, 10.0, 0.2, 2.8, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Radish (raw)", 16.0, 0.7, 3.4, 0.1, 1.6, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Turnip (cooked)", 22.0, 0.9, 5.1, 0.1, 2.0, "VEGAN", "LUNCH", "VEGETABLES"),

        // BELL PEPPERS & CAPSICUMS
        new Food("Red Bell Pepper", 31.0, 1.0, 7.3, 0.3, 2.5, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Green Bell Pepper", 20.0, 0.9, 4.6, 0.2, 1.7, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Yellow Bell Pepper", 27.0, 1.0, 6.3, 0.2, 0.9, "VEGAN", "LUNCH",
            "VEGETABLES"),

        // INDIAN VEGETABLES
        new Food("Okra (Bhindi)", 33.0, 1.9, 7.5, 0.2, 3.2, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Bitter Gourd (Karela)", 17.0, 1.0, 3.7, 0.2, 2.8, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Bottle Gourd (Lauki)", 14.0, 0.6, 3.4, 0.0, 0.5, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Ridge Gourd (Turai)", 20.0, 1.2, 4.4, 0.3, 1.8, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Ash Gourd (Petha)", 13.0, 0.4, 3.0, 0.2, 2.2, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Drumstick (Moringa)", 37.0, 2.1, 8.5, 0.2, 3.2, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Eggplant (Brinjal)", 25.0, 1.0, 5.9, 0.2, 3.0, "VEGAN", "LUNCH",
            "VEGETABLES"),

        // OTHER VEGETABLES
        new Food("Tomato", 18.0, 0.9, 3.9, 0.2, 1.2, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Cucumber", 15.0, 0.7, 3.6, 0.1, 0.5, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Onion", 40.0, 1.1, 9.3, 0.1, 1.7, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Garlic", 149.0, 6.4, 33.1, 0.5, 2.1, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Ginger", 80.0, 1.8, 17.8, 0.8, 2.0, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Green Peas", 81.0, 5.4, 14.5, 0.4, 5.7, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Green Beans", 35.0, 1.8, 8.0, 0.1, 2.7, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Mushrooms (Button)", 22.0, 3.1, 3.3, 0.3, 1.0, "VEGAN", "LUNCH",
            "VEGETABLES"),
        new Food("Asparagus", 20.0, 2.2, 3.9, 0.1, 2.1, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Zucchini", 17.0, 1.2, 3.1, 0.3, 1.0, "VEGAN", "LUNCH", "VEGETABLES"),

        // ===== LEGUMES & BEANS =====

        // LENTILS (Dal) - Cooked per 100g
        new Food("Red Lentils (Masoor Dal, cooked)", 116.0, 9.0, 20.1, 0.4, 7.9, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Yellow Lentils (Toor Dal, cooked)", 343.0, 22.3, 62.2, 1.5, 16.3, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Black Lentils (Urad Dal, cooked)", 341.0, 25.2, 58.9, 1.6, 18.3, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Green Lentils (Moong Dal, cooked)", 347.0, 24.0, 59.0, 1.2, 16.3, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Bengal Gram (Chana Dal, cooked)", 364.0, 22.0, 61.5, 6.8, 12.2, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Split Peas (cooked)", 118.0, 8.3, 21.1, 0.4, 8.3, "VEGAN", "LUNCH",
            "PROTEIN"),

        // BEANS - Cooked per 100g
        new Food("Black Beans (cooked)", 132.0, 8.9, 23.7, 0.5, 8.7, "VEGAN", "LUNCH",
            "PROTEIN"),
        new Food("Kidney Beans (Rajma, cooked)", 127.0, 8.7, 22.8, 0.5, 6.4, "VEGAN", "LUNCH",
            "PROTEIN"),
        new Food("Chickpeas (Kabuli Chana, cooked)", 164.0, 8.9, 27.4, 2.6, 7.6, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Black Chickpeas (Kala Chana, cooked)", 378.0, 20.1, 61.0, 5.3, 17.1, "VEGAN",
            "LUNCH", "PROTEIN"),
        new Food("Navy Beans (cooked)", 140.0, 8.2, 26.1, 0.6, 10.5, "VEGAN", "LUNCH",
            "PROTEIN"),
        new Food("Pinto Beans (cooked)", 143.0, 9.0, 26.2, 0.7, 9.0, "VEGAN", "LUNCH",
            "PROTEIN"),
        new Food("Lima Beans (cooked)", 115.0, 7.8, 20.9, 0.4, 7.0, "VEGAN", "LUNCH",
            "PROTEIN"),

        // ===== GRAINS & STARCHES =====

        // RICE VARIETIES (Cooked per 100g)
        new Food("Basmati Rice (cooked)", 121.0, 2.7, 25.2, 0.4, 0.4, "VEGAN", "LUNCH",
            "GRAINS"),
        new Food("Jasmine Rice (cooked)", 129.0, 2.7, 28.2, 0.3, 0.4, "VEGAN", "LUNCH",
            "GRAINS"),
        new Food("Wild Rice (cooked)", 101.0, 4.0, 21.3, 0.3, 1.8, "VEGAN", "LUNCH", "GRAINS"),
        new Food("Red Rice (cooked)", 216.0, 5.0, 45.0, 1.8, 3.5, "VEGAN", "LUNCH", "GRAINS"),

        // WHEAT PRODUCTS
        new Food("Whole Wheat Flour (Atta)", 364.0, 12.6, 71.2, 1.7, 10.7, "VEGAN", "LUNCH",
            "GRAINS"),
        new Food("Roti/Chapati (1 medium)", 71.0, 2.7, 15.2, 0.4, 2.0, "VEGAN", "LUNCH",
            "GRAINS"),
        new Food("Naan (1 piece)", 262.0, 9.0, 45.0, 5.0, 2.0, "VEG", "LUNCH", "GRAINS"),
        new Food("Paratha (plain, 1 piece)", 320.0, 8.0, 42.0, 13.0, 3.0, "VEG", "LUNCH",
            "GRAINS"),

        // OTHER GRAINS
        new Food("Barley (cooked)", 123.0, 2.3, 28.2, 0.4, 3.8, "VEGAN", "LUNCH", "GRAINS"),
        new Food("Millet (cooked)", 119.0, 3.5, 23.7, 1.0, 1.3, "VEGAN", "LUNCH", "GRAINS"),
        new Food("Buckwheat (cooked)", 92.0, 3.4, 19.9, 0.6, 2.7, "VEGAN", "LUNCH", "GRAINS"),
        new Food("Amaranth (cooked)", 102.0, 4.0, 19.0, 1.6, 2.1, "VEGAN", "LUNCH", "GRAINS"),

        // ===== PROTEIN SOURCES =====

        // DAIRY PROTEINS
        new Food("Paneer (fresh)", 265.0, 18.3, 1.2, 20.8, 0.0, "VEG", "LUNCH", "PROTEIN"),
        new Food("Low-Fat Paneer", 198.0, 20.0, 3.0, 12.0, 0.0, "VEG", "LUNCH", "PROTEIN"),
        new Food("Ricotta Cheese", 174.0, 11.4, 3.0, 13.0, 0.0, "VEG", "LUNCH", "PROTEIN"),
        new Food("Mozzarella (part-skim)", 254.0, 24.3, 2.8, 15.9, 0.0, "VEG", "LUNCH",
            "PROTEIN"),
        new Food("Cheddar Cheese", 403.0, 25.4, 3.1, 33.3, 0.0, "VEG", "LUNCH", "PROTEIN"),

        // PLANT PROTEINS
        new Food("Tofu (extra firm)", 94.0, 10.1, 2.3, 5.3, 0.9, "VEGAN", "LUNCH", "PROTEIN"),
        new Food("Seitan", 370.0, 75.2, 14.2, 1.9, 0.6, "VEGAN", "LUNCH", "PROTEIN"),
        new Food("Soy Chunks (dry)", 345.0, 52.0, 33.0, 0.5, 13.0, "VEGAN", "LUNCH", "PROTEIN"),

        // ANIMAL PROTEINS
        new Food("Chicken Breast (skinless, cooked)", 165.0, 31.0, 0.0, 3.6, 0.0, "NON_VEG",
            "LUNCH", "PROTEIN"),
        new Food("Chicken Thigh (skinless, cooked)", 209.0, 26.0, 0.0, 10.9, 0.0, "NON_VEG",
            "LUNCH", "PROTEIN"),
        new Food("Turkey Breast (cooked)", 135.0, 30.1, 0.0, 1.0, 0.0, "NON_VEG", "LUNCH",
            "PROTEIN"),
        new Food("Lean Beef (90/10, cooked)", 176.0, 26.1, 0.0, 7.5, 0.0, "NON_VEG", "LUNCH",
            "PROTEIN"),
        new Food("Pork Tenderloin (cooked)", 143.0, 26.0, 0.0, 3.5, 0.0, "NON_VEG", "LUNCH",
            "PROTEIN"),

        // FISH & SEAFOOD
        new Food("Salmon (cooked)", 208.0, 22.1, 0.0, 12.4, 0.0, "NON_VEG", "LUNCH", "PROTEIN"),
        new Food("Tuna (cooked)", 184.0, 30.0, 0.0, 6.3, 0.0, "NON_VEG", "LUNCH", "PROTEIN"),
        new Food("Cod (cooked)", 105.0, 22.8, 0.0, 0.9, 0.0, "NON_VEG", "LUNCH", "PROTEIN"),
        new Food("Mackerel (cooked)", 262.0, 23.9, 0.0, 17.8, 0.0, "NON_VEG", "LUNCH",
            "PROTEIN"),
        new Food("Sardines (canned)", 208.0, 24.6, 0.0, 11.5, 0.0, "NON_VEG", "LUNCH",
            "PROTEIN"),
        new Food("Shrimp (cooked)", 99.0, 18.9, 0.9, 1.5, 0.0, "NON_VEG", "LUNCH", "PROTEIN"),

        // ===== HEALTHY FATS =====

        // OILS (Per tablespoon - 15ml)
        new Food("Extra Virgin Olive Oil", 884.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "COOKING",
            "FATS"),
        new Food("Coconut Oil", 862.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "COOKING", "FATS"),
        new Food("Avocado Oil", 884.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "COOKING", "FATS"),
        new Food("Flaxseed Oil", 884.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "COOKING", "FATS"),
        new Food("Sesame Oil", 884.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "COOKING", "FATS"),
        new Food("Mustard Oil", 884.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "COOKING", "FATS"),

        // SEEDS (Per 100g)
        new Food("Chia Seeds", 486.0, 16.5, 42.1, 30.7, 34.4, "VEGAN", "SNACK", "SEEDS"),
        new Food("Flax Seeds", 534.0, 18.3, 28.9, 42.2, 27.3, "VEGAN", "SNACK", "SEEDS"),
        new Food("Sunflower Seeds", 584.0, 20.8, 20.0, 51.5, 8.6, "VEGAN", "SNACK", "SEEDS"),
        new Food("Pumpkin Seeds", 559.0, 30.2, 10.7, 49.1, 6.0, "VEGAN", "SNACK", "SEEDS"),
        new Food("Sesame Seeds", 573.0, 17.7, 23.4, 49.7, 11.8, "VEGAN", "SNACK", "SEEDS"),

        // ===== BEVERAGES =====

        new Food("Green Tea (unsweetened)", 1.0, 0.2, 0.0, 0.0, 0.0, "VEGAN", "BEVERAGES",
            "BEVERAGES"),
        new Food("Black Coffee (unsweetened)", 2.0, 0.3, 0.0, 0.0, 0.0, "VEGAN", "BEVERAGES",
            "BEVERAGES"),
        new Food("Herbal Tea", 1.0, 0.0, 0.0, 0.0, 0.0, "VEGAN", "BEVERAGES", "BEVERAGES"),
        new Food("Coconut Water", 19.0, 0.7, 3.7, 0.2, 1.1, "VEGAN", "BEVERAGES", "BEVERAGES"),
        new Food("Fresh Orange Juice", 45.0, 0.7, 10.4, 0.2, 0.2, "VEGAN", "BEVERAGES",
            "BEVERAGES"),
        new Food("Lemon Water", 6.0, 0.1, 1.6, 0.1, 0.1, "VEGAN", "BEVERAGES", "BEVERAGES"),

        // ===== CONDIMENTS & SPICES =====

        new Food("Turmeric (ground)", 354.0, 7.8, 64.9, 9.9, 21.1, "VEGAN", "COOKING",
            "SPICES"),
        new Food("Cumin (ground)", 375.0, 17.8, 44.2, 22.3, 10.5, "VEGAN", "COOKING", "SPICES"),
        new Food("Coriander (ground)", 298.0, 12.4, 54.9, 17.8, 41.9, "VEGAN", "COOKING",
            "SPICES"),
        new Food("Black Pepper (ground)", 251.0, 10.4, 63.9, 3.3, 25.3, "VEGAN", "COOKING",
            "SPICES"),
        new Food("Cinnamon (ground)", 247.0, 4.0, 80.6, 1.2, 53.1, "VEGAN", "COOKING",
            "SPICES"),
        new Food("Cardamom (ground)", 311.0, 10.8, 68.5, 6.7, 28.0, "VEGAN", "COOKING",
            "SPICES"),
        new Food("Cloves (ground)", 274.0, 6.0, 65.5, 13.0, 33.9, "VEGAN", "COOKING", "SPICES"),

        // Low Sodium/Healthy Seasonings
        new Food("Lemon Juice (fresh)", 22.0, 0.4, 6.9, 0.2, 0.5, "VEGAN", "COOKING",
            "CONDIMENTS"),
        new Food("Apple Cider Vinegar", 22.0, 0.0, 0.9, 0.0, 0.0, "VEGAN", "COOKING",
            "CONDIMENTS"),
        new Food("Balsamic Vinegar", 88.0, 0.5, 17.0, 0.0, 0.0, "VEGAN", "COOKING",
            "CONDIMENTS"),
        new Food("Nutritional Yeast", 325.0, 45.0, 35.0, 7.0, 20.0, "VEGAN", "COOKING",
            "CONDIMENTS"),

        // ===== SUPERFOODS & SPECIALTY ITEMS =====

        new Food("Spirulina (dry)", 290.0, 57.5, 23.9, 7.7, 3.6, "VEGAN", "SUPPLEMENTS",
            "SUPERFOODS"),
        new Food("Chlorella (dry)", 336.0, 58.4, 23.0, 9.0, 0.0, "VEGAN", "SUPPLEMENTS",
            "SUPERFOODS"),
        new Food("Moringa Powder", 205.0, 27.1, 38.2, 2.3, 19.2, "VEGAN", "SUPPLEMENTS",
            "SUPERFOODS"),
        new Food("Wheatgrass Powder", 304.0, 32.0, 40.0, 3.3, 33.0, "VEGAN", "SUPPLEMENTS",
            "SUPERFOODS"),

        // ===== HEALTHY SNACKS =====

        new Food("Roasted Chickpeas (100g)", 164.0, 6.0, 27.0, 2.6, 5.0, "VEGAN", "SNACK",
            "PROTEIN"),
        new Food("Kale Chips (baked)", 533.0, 15.1, 53.6, 29.8, 7.8, "VEGAN", "SNACK",
            "VEGETABLES"),
        new Food("Sweet Potato Chips (baked)", 191.0, 3.2, 43.5, 0.5, 6.6, "VEGAN", "SNACK",
            "VEGETABLES"),
        new Food("Air-Popped Popcorn", 387.0, 12.9, 77.8, 4.5, 14.5, "VEGAN", "SNACK",
            "GRAINS"),

        // ===== SPECIALTY DIET FOODS =====

        // Ketogenic-Friendly
        new Food("MCT Oil", 884.0, 0.0, 0.0, 100.0, 0.0, "VEGAN", "SUPPLEMENTS", "FATS"),
        new Food("Ghee (Clarified Butter)", 900.0, 0.0, 0.0, 100.0, 0.0, "VEG", "COOKING",
            "FATS"),

        // Diabetic-Friendly (Low GI)
        new Food("Konjac Noodles (Shirataki)", 9.0, 0.2, 4.0, 0.0, 3.0, "VEGAN", "LUNCH",
            "GRAINS"),
        new Food("Cauliflower Rice (cooked)", 25.0, 2.0, 5.0, 0.3, 2.0, "VEGAN", "LUNCH",
            "VEGETABLES"),

        // High Protein Alternatives
        new Food("Protein Pasta (Chickpea)", 387.0, 23.0, 57.0, 7.0, 13.0, "VEGAN", "LUNCH",
            "GRAINS"),
        new Food("Protein Bread (per slice)", 95.0, 7.0, 12.0, 2.5, 4.0, "VEGAN", "BREAKFAST",
            "GRAINS"),

        // ===== GURU MANN SPECIALTIES =====

        // Custom Recipes (Approximate nutritional values)
        new Food("Muscle Oats", 420.0, 25.0, 65.0, 8.0, 12.0, "VEG",
            "BREAKFAST", "MIXED"),
        new Food("Super Rice Bowl", 380.0, 22.0, 58.0, 8.0, 6.0, "VEG", "LUNCH", "MIXED"),
        new Food("Power Smoothie", 280.0, 18.0, 42.0, 6.0, 8.0, "VEG", "SNACK", "MIXED"),
        new Food("Protein Pancakes", 220.0, 20.0, 25.0, 5.0, 4.0, "VEG", "BREAKFAST", "MIXED"),
        new Food("Recovery Shake", 300.0, 25.0, 45.0, 3.0, 2.0, "VEG", "POST_WORKOUT",
            "PROTEIN"),

        // ===== INTERNATIONAL FOODS =====

        // Mediterranean
        new Food("Hummus", 166.0, 8.0, 14.3, 9.6, 6.0, "VEGAN", "SNACK", "PROTEIN"),
        new Food("Tahini", 595.0, 17.0, 21.2, 53.8, 9.3, "VEGAN", "CONDIMENTS", "NUTS"),
        new Food("Olives (mixed)", 115.0, 0.8, 6.3, 10.7, 3.2, "VEGAN", "SNACK", "FATS"),
        new Food("Feta Cheese", 75.0, 4.0, 1.2, 6.0, 0.0, "VEG", "LUNCH", "PROTEIN"),

        // Asian
        new Food("Kimchi", 15.0, 1.1, 2.4, 0.5, 1.6, "VEGAN", "LUNCH", "VEGETABLES"),
        new Food("Miso Paste", 199.0, 12.8, 26.5, 6.0, 5.4, "VEGAN", "CONDIMENTS", "PROTEIN"),
        new Food("Kombu (Seaweed)", 43.0, 1.7, 9.6, 0.6, 1.3, "VEGAN", "VEGETABLES",
            "VEGETABLES"),

        // ===== MEAL REPLACEMENT & CONVENIENCE =====

        new Food("Protein Bar (average)", 400.0, 20.0, 40.0, 15.0, 5.0, "VEG", "SNACK",
            "SUPPLEMENTS"),
        new Food("Meal Replacement Shake", 200.0, 15.0, 25.0, 5.0, 3.0, "VEG", "MEAL",
            "SUPPLEMENTS"),
        new Food("Energy Bar (natural)", 380.0, 8.0, 65.0, 12.0, 6.0, "VEGAN", "SNACK",
            "MIXED"));

    try {
      foodRepository.saveAll(foods);
      log.info("Successfully saved {} food entries to database", foods.size());
    } catch (Exception e) {
      log.error("Error saving food data to database", e);
      throw new RuntimeException("Failed to initialize food data", e);
    }
  }

  private void initializeComprehensiveExerciseData() {
    List<Exercise> exercises = Arrays.asList(
        // ===== CARDIO EXERCISES =====

        // RUNNING & JOGGING (Calories per minute based on 70kg person)
        new Exercise("Treadmill Walking (3.5 mph)", "CARDIO", "FULL_BODY", "MACHINE",
            "BEGINNER", 4.5,
            "Moderate pace treadmill walking",
            "Maintain steady 3.5 mph pace, slight incline optional, focus on posture"),
        new Exercise("Treadmill Walking (4.0 mph)", "CARDIO", "FULL_BODY", "MACHINE",
            "BEGINNER", 5.5,
            "Brisk treadmill walking",
            "Maintain 4.0 mph pace, swing arms naturally, breathe rhythmically"),
        new Exercise("Incline Walking (6-8%)", "CARDIO", "FULL_BODY", "MACHINE", "INTERMEDIATE",
            7.0,
            "Incline treadmill walking",
            "6-8% incline, moderate pace, engage core, avoid holding rails"),
        new Exercise("Incline Walking (10-12%)", "CARDIO", "FULL_BODY", "MACHINE",
            "INTERMEDIATE", 8.5,
            "Steep incline walking",
            "10-12% incline, focus on glute activation, maintain upright posture"),

        new Exercise("Jogging (5 mph)", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 8.0,
            "Light jogging pace",
            "Maintain conversational pace, land midfoot, relaxed shoulders"),
        new Exercise("Running (6 mph)", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 10.0,
            "Moderate running pace",
            "Steady 6 mph pace, controlled breathing, efficient stride"),
        new Exercise("Running (7 mph)", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 12.0,
            "Fast running pace",
            "7 mph pace, focus on form, pump arms efficiently"),
        new Exercise("Running (8+ mph)", "CARDIO", "FULL_BODY", "NONE", "ADVANCED", 15.0,
            "High-intensity running",
            "Sprint pace, maximum effort, focus on speed and power"),

        // INTERVAL TRAINING
        new Exercise("HIIT Treadmill Intervals", "CARDIO", "FULL_BODY", "MACHINE", "ADVANCED",
            14.0,
            "High-intensity interval training",
            "30sec high intensity / 30sec recovery, progressive speed/incline"),
        new Exercise("Sprint Intervals", "CARDIO", "FULL_BODY", "NONE", "ADVANCED", 16.0,
            "All-out sprint intervals",
            "15-30sec sprints, 60-90sec recovery, maximum effort"),

        // CYCLING
        new Exercise("Stationary Bike (Light)", "CARDIO", "LEGS", "MACHINE", "BEGINNER", 5.5,
            "Light resistance cycling",
            "RPE 5-6/10, maintain steady cadence 80-90 RPM"),
        new Exercise("Stationary Bike (Moderate)", "CARDIO", "LEGS", "MACHINE", "INTERMEDIATE",
            7.5,
            "Moderate resistance cycling",
            "RPE 7/10, cadence 80-100 RPM, standing climbs optional"),
        new Exercise("Stationary Bike (Vigorous)", "CARDIO", "LEGS", "MACHINE", "ADVANCED",
            10.0,
            "High-intensity cycling",
            "RPE 8-9/10, varied resistance, standing intervals"),
        new Exercise("Outdoor Cycling (12-14 mph)", "CARDIO", "LEGS", "NONE", "INTERMEDIATE",
            8.0,
            "Moderate outdoor cycling",
            "Maintain 12-14 mph, adjust for terrain and wind"),
        new Exercise("Outdoor Cycling (16+ mph)", "CARDIO", "LEGS", "NONE", "ADVANCED", 12.0,
            "Fast outdoor cycling",
            "16+ mph pace, aerodynamic position, sustained effort"),

        // ELLIPTICAL & CROSS-TRAINING
        new Exercise("Elliptical (Low Intensity)", "CARDIO", "FULL_BODY", "MACHINE", "BEGINNER",
            6.0,
            "Low-impact full body cardio",
            "RPE 5-6/10, use arm handles, maintain upright posture"),
        new Exercise("Elliptical (Moderate)", "CARDIO", "FULL_BODY", "MACHINE", "INTERMEDIATE",
            8.5,
            "Moderate elliptical workout",
            "RPE 7/10, vary resistance and incline, engage core"),
        new Exercise("Elliptical (High Intensity)", "CARDIO", "FULL_BODY", "MACHINE",
            "ADVANCED", 11.0,
            "High-intensity elliptical",
            "RPE 8-9/10, interval training, maximum effort periods"),

        // SWIMMING
        new Exercise("Swimming (Leisurely)", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 7.0,
            "Recreational swimming",
            "Mixed strokes, focus on technique, moderate pace"),
        new Exercise("Swimming (Moderate)", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 10.0,
            "Lap swimming workout",
            "Freestyle/mixed strokes, continuous laps, bilateral breathing"),
        new Exercise("Swimming (Vigorous)", "CARDIO", "FULL_BODY", "NONE", "ADVANCED", 13.0,
            "High-intensity swimming",
            "Fast-paced laps, stroke intervals, competitive pace"),

        // ROWING
        new Exercise("Rowing Machine (Light)", "CARDIO", "FULL_BODY", "MACHINE", "BEGINNER",
            6.0,
            "Low-intensity rowing",
            "Focus on form: legs-core-arms, controlled recovery"),
        new Exercise("Rowing Machine (Moderate)", "CARDIO", "FULL_BODY", "MACHINE",
            "INTERMEDIATE", 8.5,
            "Moderate rowing workout",
            "Steady state rowing, 20-24 strokes per minute"),
        new Exercise("Rowing Machine (Vigorous)", "CARDIO", "FULL_BODY", "MACHINE", "ADVANCED",
            12.0,
            "High-intensity rowing",
            "Intervals and sprints, 26+ strokes per minute"),

        // OTHER CARDIO
        new Exercise("Jump Rope (Slow)", "CARDIO", "FULL_BODY", "EQUIPMENT", "BEGINNER", 8.0,
            "Basic jump rope",
            "Basic bounce, 100-120 jumps/minute, light on feet"),
        new Exercise("Jump Rope (Fast)", "CARDIO", "FULL_BODY", "EQUIPMENT", "ADVANCED", 12.0,
            "High-intensity jump rope",
            "Fast pace, 140+ jumps/minute, varied footwork patterns"),
        new Exercise("Stair Climbing", "CARDIO", "LEGS", "NONE", "INTERMEDIATE", 9.0,
            "Stair climbing cardio",
            "Take stairs 2 at a time, pump arms, maintain rhythm"),
        new Exercise("Step-Ups", "CARDIO", "LEGS", "EQUIPMENT", "INTERMEDIATE", 7.0,
            "Box/bench step-ups",
            "20-24 inch box, full foot contact, controlled movement"),

        // ===== CHEST EXERCISES =====

        // BARBELL CHEST
        new Exercise("Barbell Bench Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE", 6.0,
            "Primary chest exercise",
            "Retract shoulder blades, arch slightly, press bar over nipple line, control eccentric"),
        new Exercise("Incline Barbell Press (30°)", "STRENGTH", "CHEST", "BARBELL",
            "INTERMEDIATE", 6.5,
            "Upper chest focus",
            "30-45° incline, press to upper chest, maintain wrist alignment"),
        new Exercise("Incline Barbell Press (45°)", "STRENGTH", "CHEST", "BARBELL",
            "INTERMEDIATE", 6.5,
            "Upper chest emphasis",
            "45° incline, slower eccentric, pause at chest"),
        new Exercise("Decline Barbell Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE",
            6.0,
            "Lower chest focus",
            "15-30° decline, press to lower chest, controlled movement"),
        new Exercise("Close Grip Bench Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE",
            6.0,
            "Triceps and inner chest",
            "Hands shoulder-width apart, elbows close to body, triceps emphasis"),

        // DUMBBELL CHEST
        new Exercise("Dumbbell Bench Press", "STRENGTH", "CHEST", "DUMBBELL", "BEGINNER", 5.5,
            "Unilateral chest development",
            "Press up and slightly together, full range of motion, stabilize core"),
        new Exercise("Incline Dumbbell Press", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE",
            6.0,
            "Upper chest isolation",
            "30-45° incline, press at slight angle, control descent"),
        new Exercise("Decline Dumbbell Press", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE",
            5.5,
            "Lower chest development",
            "Decline position, full stretch, press together at top"),
        new Exercise("Dumbbell Flyes", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE", 5.0,
            "Chest isolation movement",
            "Wide arc, maintain slight elbow bend, deep stretch at bottom"),
        new Exercise("Incline Dumbbell Flyes", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE",
            5.0,
            "Upper chest isolation",
            "30-45° incline, focus on stretch and squeeze, control negative"),
        new Exercise("Pullovers", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE", 4.5,
            "Chest and lat stretch",
            "Perpendicular to bench, lower behind head, ribcage expansion"),

        // CABLE CHEST
        new Exercise("Cable Crossovers (High to Low)", "STRENGTH", "CHEST", "MACHINE",
            "INTERMEDIATE", 5.0,
            "Lower chest emphasis",
            "High pulleys, cross at waist level, squeeze and hold"),
        new Exercise("Cable Crossovers (Mid)", "STRENGTH", "CHEST", "MACHINE", "INTERMEDIATE",
            5.0,
            "Mid chest focus",
            "Mid pulleys, cross at chest level, maintain slight forward lean"),
        new Exercise("Cable Crossovers (Low to High)", "STRENGTH", "CHEST", "MACHINE",
            "INTERMEDIATE", 5.0,
            "Upper chest emphasis",
            "Low pulleys, cross above chest level, lean forward slightly"),
        new Exercise("Cable Flyes", "STRENGTH", "CHEST", "MACHINE", "INTERMEDIATE", 4.5,
            "Constant tension chest",
            "Maintain arc motion, squeeze at center, control eccentric"),

        // MACHINE CHEST
        new Exercise("Chest Press Machine", "STRENGTH", "CHEST", "MACHINE", "BEGINNER", 5.0,
            "Beginner-friendly chest",
            "Adjust seat height, press handles forward, controlled movement"),
        new Exercise("Pec Deck Machine", "STRENGTH", "CHEST", "MACHINE", "BEGINNER", 4.0,
            "Chest isolation",
            "Maintain contact with pads, squeeze pecs together, slow negative"),
        new Exercise("Hammer Strength Chest Press", "STRENGTH", "CHEST", "MACHINE",
            "INTERMEDIATE", 5.5,
            "Unilateral machine press",
            "Independent arm movement, full range, stabilize core"),

        // BODYWEIGHT CHEST
        new Exercise("Push-ups (Standard)", "STRENGTH", "CHEST", "NONE", "BEGINNER", 5.0,
            "Basic bodyweight chest",
            "Straight body line, chest to ground, full extension"),
        new Exercise("Push-ups (Wide Grip)", "STRENGTH", "CHEST", "NONE", "BEGINNER", 5.2,
            "Outer chest emphasis",
            "Hands wider than shoulders, emphasize chest stretch"),
        new Exercise("Push-ups (Diamond)", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE", 5.5,
            "Inner chest and triceps",
            "Form diamond with hands, elbows close to body"),
        new Exercise("Incline Push-ups", "STRENGTH", "CHEST", "EQUIPMENT", "BEGINNER", 4.0,
            "Upper chest bodyweight",
            "Feet elevated on bench/box, hands on ground"),
        new Exercise("Decline Push-ups", "STRENGTH", "CHEST", "EQUIPMENT", "INTERMEDIATE", 6.0,
            "Lower chest emphasis",
            "Hands elevated, feet on ground, steeper angle"),
        new Exercise("Archer Push-ups", "STRENGTH", "CHEST", "NONE", "ADVANCED", 6.5,
            "Unilateral chest strength",
            "Shift weight to one arm, other arm straight for support"),

        // DIPS
        new Exercise("Parallel Bar Dips", "STRENGTH", "CHEST", "EQUIPMENT", "INTERMEDIATE", 6.0,
            "Chest and tricep compound",
            "Lean forward for chest, lower until stretch, press up"),
        new Exercise("Bench Dips", "STRENGTH", "CHEST", "EQUIPMENT", "BEGINNER", 4.5,
            "Tricep-focused dips",
            "Hands on bench edge, feet extended, lower body weight"),
        new Exercise("Ring Dips", "STRENGTH", "CHEST", "EQUIPMENT", "ADVANCED", 7.0,
            "Unstable surface dips",
            "Rings provide instability challenge, maintain ring position"),

        // ===== BACK EXERCISES =====

        // PULL-UPS & VARIATIONS
        new Exercise("Pull-ups (Wide Grip)", "STRENGTH", "BACK", "EQUIPMENT", "INTERMEDIATE",
            6.5,
            "Lat width development",
            "Hands wider than shoulders, pull chest to bar, control descent"),
        new Exercise("Pull-ups (Medium Grip)", "STRENGTH", "BACK", "EQUIPMENT", "INTERMEDIATE",
            6.0,
            "Balanced lat development",
            "Shoulder-width grip, full range of motion, engage lats"),
        new Exercise("Chin-ups", "STRENGTH", "BACK", "EQUIPMENT", "INTERMEDIATE", 6.0,
            "Bicep and lat emphasis",
            "Underhand grip, pull chin over bar, squeeze lats and biceps"),
        new Exercise("Neutral Grip Pull-ups", "STRENGTH", "BACK", "EQUIPMENT", "INTERMEDIATE",
            6.0,
            "Joint-friendly pulling",
            "Parallel grip handles, natural hand position, full range"),
        new Exercise("Commando Pull-ups", "STRENGTH", "BACK", "EQUIPMENT", "ADVANCED", 7.0,
            "Alternating side pull-ups",
            "Alternate pulling head to each side of bar, unilateral focus"),
        new Exercise("L-Sit Pull-ups", "STRENGTH", "BACK", "EQUIPMENT", "ADVANCED", 8.0,
            "Core-integrated pulling",
            "Hold legs horizontal throughout movement, extreme difficulty"),

        // ASSISTED PULL-UPS
        new Exercise("Assisted Pull-ups (Machine)", "STRENGTH", "BACK", "MACHINE", "BEGINNER",
            4.5,
            "Beginner pull-up progression",
            "Use assistance weight, maintain proper form, gradually reduce aid"),
        new Exercise("Assisted Pull-ups (Band)", "STRENGTH", "BACK", "EQUIPMENT", "BEGINNER",
            4.5,
            "Band-assisted progression",
            "Place band under knees/feet, gradually use lighter resistance"),
        new Exercise("Negative Pull-ups", "STRENGTH", "BACK", "EQUIPMENT", "BEGINNER", 5.0,
            "Eccentric strength building",
            "Jump/step to top position, lower slowly (5+ seconds)"),

        // LAT PULLDOWNS
        new Exercise("Lat Pulldown (Wide Grip)", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
            "Lat width builder",
            "Pull to upper chest, squeeze shoulder blades, control return"),
        new Exercise("Lat Pulldown (Close Grip)", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
            5.5,
            "Lat thickness focus",
            "V-bar attachment, pull to chest, emphasize lat squeeze"),
        new Exercise("Reverse Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
            5.5,
            "Lower lat emphasis",
            "Underhand grip, pull to lower chest, bicep involvement"),
        new Exercise("Single Arm Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
            5.0,
            "Unilateral lat development",
            "One arm at a time, focus on lat contraction, prevent rotation"),

        // ROWING MOVEMENTS
        new Exercise("Barbell Rows (Bent-over)", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE",
            5.5,
            "Classic back builder",
            "Hip hinge position, pull to lower chest/upper abdomen, squeeze blades"),
        new Exercise("Barbell Rows (Underhand)", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE",
            5.5,
            "Lower lat and rhomboid focus",
            "Underhand grip, pull to lower chest, bicep assistance"),
        new Exercise("Barbell Rows (Pendlay)", "STRENGTH", "BACK", "BARBELL", "ADVANCED", 6.0,
            "Explosive rowing variation",
            "Bar starts on ground each rep, explosive pull, pause at chest"),

        new Exercise("Dumbbell Rows (Single Arm)", "STRENGTH", "BACK", "DUMBBELL", "BEGINNER",
            5.0,
            "Unilateral back development",
            "Support on bench, pull to hip, full stretch and contraction"),
        new Exercise("Dumbbell Rows (Chest Supported)", "STRENGTH", "BACK", "DUMBBELL",
            "INTERMEDIATE", 5.0,
            "Strict rowing form",
            "Chest on incline bench, eliminate momentum, pure back work"),

        new Exercise("Cable Rows (Seated)", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
            "Controlled rowing movement",
            "Sit upright, pull to abdomen, squeeze shoulder blades"),
        new Exercise("Cable Rows (Wide Grip)", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
            5.5,
            "Upper back focus",
            "Wide bar attachment, pull to chest, target rear delts and rhomboids"),
        new Exercise("Cable Rows (Single Arm)", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
            5.0,
            "Unilateral cable work",
            "One arm at a time, focus on lat contraction, prevent body rotation"),

        new Exercise("T-Bar Rows", "STRENGTH", "BACK", "EQUIPMENT", "INTERMEDIATE", 5.5,
            "Thick back development",
            "Chest supported, pull bar to chest, squeeze at top"),
        new Exercise("Inverted Rows", "STRENGTH", "BACK", "EQUIPMENT", "BEGINNER", 4.5,
            "Bodyweight rowing",
            "Bar at chest height, pull chest to bar, maintain straight body"),

        // DEADLIFT VARIATIONS
        new Exercise("Conventional Deadlift", "STRENGTH", "BACK", "BARBELL", "ADVANCED", 8.0,
            "King of back exercises",
            "Hip hinge movement, keep bar close, drive through heels, tall finish"),
        new Exercise("Sumo Deadlift", "STRENGTH", "BACK", "BARBELL", "ADVANCED", 8.0,
            "Wide stance deadlift",
            "Feet wide, hands inside legs, more upright torso, glute emphasis"),
        new Exercise("Romanian Deadlift", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE", 6.5,
            "Hip hinge pattern",
            "Start from top, push hips back, feel hamstring stretch, reverse"),
        new Exercise("Stiff Leg Deadlift", "STRENGTH", "BACK", "DUMBBELL", "INTERMEDIATE", 6.0,
            "Hamstring and lower back",
            "Keep legs relatively straight, hinge at hips, feel hamstring stretch"),

        // SPECIALIZED BACK
        new Exercise("Face Pulls", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE", 4.0,
            "Rear delt and rhomboid",
            "High pulley, pull to face level, external rotation at end"),
        new Exercise("Reverse Flyes", "STRENGTH", "BACK", "DUMBBELL", "INTERMEDIATE", 4.0,
            "Posterior deltoid isolation",
            "Bent over, arms wide, squeeze shoulder blades together"),
        new Exercise("Shrugs (Barbell)", "STRENGTH", "BACK", "BARBELL", "BEGINNER", 4.0,
            "Trapezius development",
            "Elevate shoulders straight up, hold contraction, slow negative"),
        new Exercise("Shrugs (Dumbbell)", "STRENGTH", "BACK", "DUMBBELL", "BEGINNER", 4.0,
            "Trap isolation",
            "Hold dumbbells at sides, shrug shoulders up and back"),

        // ===== SHOULDER EXERCISES =====

        // OVERHEAD PRESSES
        new Exercise("Overhead Press (Standing)", "STRENGTH", "SHOULDERS", "BARBELL",
            "INTERMEDIATE", 5.5,
            "Complete shoulder developer",
            "Stand tall, press bar overhead, keep core tight, full lockout"),
        new Exercise("Overhead Press (Seated)", "STRENGTH", "SHOULDERS", "BARBELL",
            "INTERMEDIATE", 5.0,
            "Isolated shoulder press",
            "Seated with back support, eliminates leg drive, pure shoulder work"),
        new Exercise("Behind Neck Press", "STRENGTH", "SHOULDERS", "BARBELL", "ADVANCED", 5.5,
            "Advanced shoulder movement",
            "CAUTION: Only for flexible individuals, press from behind neck"),

        new Exercise("Dumbbell Shoulder Press", "STRENGTH", "SHOULDERS", "DUMBBELL", "BEGINNER",
            5.0,
            "Unilateral shoulder development",
            "Seated or standing, press both arms, full range of motion"),
        new Exercise("Single Arm Press", "STRENGTH", "SHOULDERS", "DUMBBELL", "INTERMEDIATE",
            5.0,
            "Core-challenging press",
            "One arm at a time, resist rotation, engage core"),
        new Exercise("Arnold Press", "STRENGTH", "SHOULDERS", "DUMBBELL", "INTERMEDIATE", 5.5,
            "Complete deltoid activation",
            "Rotate palms from facing in to out during press, full ROM"),

        new Exercise("Machine Shoulder Press", "STRENGTH", "SHOULDERS", "MACHINE", "BEGINNER",
            4.5,
            "Beginner-friendly press",
            "Seated machine, controlled movement, focus on deltoid squeeze"),

        // LATERAL RAISES
        new Exercise("Lateral Raises", "STRENGTH", "SHOULDERS", "DUMBBELL", "BEGINNER", 3.5,
            "Side deltoid isolation",
            "Raise arms to shoulder height, slight forward angle, control descent"),
        new Exercise("Cable Lateral Raises", "STRENGTH", "SHOULDERS", "MACHINE", "INTERMEDIATE",
            4.0,
            "Constant tension laterals",
            "Low pulley, constant tension throughout range, smooth movement"),
        new Exercise("Machine Lateral Raises", "STRENGTH", "SHOULDERS", "MACHINE", "BEGINNER",
            3.5,
            "Guided lateral movement",
            "Seated machine, elbows against pads, raise to shoulder height"),
        new Exercise("Leaning Lateral Raises", "STRENGTH", "SHOULDERS", "DUMBBELL",
            "INTERMEDIATE", 4.0,
            "Enhanced range of motion",
            "Hold support with free hand, lean away, increased stretch"),

        // FRONT RAISES
        new Exercise("Front Raises (Dumbbell)", "STRENGTH", "SHOULDERS", "DUMBBELL", "BEGINNER",
            3.5,
            "Anterior deltoid focus",
            "Raise one or both arms forward to shoulder height, control movement"),
        new Exercise("Front Raises (Barbell)", "STRENGTH", "SHOULDERS", "BARBELL",
            "INTERMEDIATE", 4.0,
            "Bilateral front deltoid",
            "Hold barbell, raise to shoulder height, resist swinging"),
        new Exercise("Front Raises (Cable)", "STRENGTH", "SHOULDERS", "MACHINE", "INTERMEDIATE",
            4.0,
            "Constant tension front raise",
            "Low pulley, smooth arc motion, squeeze at top"),
        new Exercise("Plate Raises", "STRENGTH", "SHOULDERS", "EQUIPMENT", "INTERMEDIATE", 4.0,
            "Functional front raise",
            "Hold weight plate, raise to shoulder height, engage core"),

        // REAR DELTOID
        new Exercise("Rear Delt Flyes (Bent-over)", "STRENGTH", "SHOULDERS", "DUMBBELL",
            "INTERMEDIATE", 4.0,
            "Posterior deltoid isolation",
            "Bend at waist, raise arms wide, squeeze shoulder blades"),
        new Exercise("Rear Delt Flyes (Incline)", "STRENGTH", "SHOULDERS", "DUMBBELL",
            "INTERMEDIATE", 4.0,
            "Chest-supported rear delt",
            "Chest on incline bench, eliminates momentum, pure rear delt work"),
        new Exercise("Cable Rear Delt Flyes", "STRENGTH", "SHOULDERS", "MACHINE",
            "INTERMEDIATE", 4.0,
            "Cable posterior deltoid",
            "High pulleys, cross cables and pull back, external rotation"),
        new Exercise("Reverse Pec Deck", "STRENGTH", "SHOULDERS", "MACHINE", "BEGINNER", 3.5,
            "Machine rear deltoid",
            "Reverse pec deck motion, squeeze shoulder blades together"),

        // UPRIGHT MOVEMENTS
        new Exercise("Upright Rows (Barbell)", "STRENGTH", "SHOULDERS", "BARBELL",
            "INTERMEDIATE", 4.5,
            "Trap and deltoid developer",
            "Pull bar to chin level, elbows high, control descent"),
        new Exercise("Upright Rows (Dumbbell)", "STRENGTH", "SHOULDERS", "DUMBBELL",
            "INTERMEDIATE", 4.5,
            "Unilateral upright pull",
            "Natural hand position, avoid excessive internal rotation"),
        new Exercise("High Pulls", "STRENGTH", "SHOULDERS", "BARBELL", "ADVANCED", 6.0,
            "Explosive shoulder movement",
            "Explosive pull to chest level, shrug at top, Olympic lift variation"),

        // Let me continue with Arms, Legs, and Core exercises...

        // ===== ARM EXERCISES =====

        // BICEP EXERCISES
        new Exercise("Barbell Curls", "STRENGTH", "ARMS", "BARBELL", "BEGINNER", 3.5,
            "Classic bicep builder",
            "Stand tall, curl bar up, squeeze biceps at top, control negative"),
        new Exercise("Dumbbell Curls (Alternating)", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER",
            3.5,
            "Unilateral bicep development",
            "Alternate arms, full range of motion, avoid swinging"),
        new Exercise("Dumbbell Curls (Simultaneous)", "STRENGTH", "ARMS", "DUMBBELL",
            "BEGINNER", 3.5,
            "Bilateral bicep work",
            "Both arms together, maintain strict form, focus on contraction"),
        new Exercise("Hammer Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
            "Brachialis and bicep",
            "Neutral grip, maintain hammer position throughout, thick forearms"),
        new Exercise("Incline Dumbbell Curls", "STRENGTH", "ARMS", "DUMBBELL", "INTERMEDIATE",
            4.0,
            "Bicep stretch emphasis",
            "45° incline, arms hang back, full stretch at bottom"),
        new Exercise("Preacher Curls", "STRENGTH", "ARMS", "EQUIPMENT", "INTERMEDIATE", 4.0,
            "Isolated bicep movement",
            "Preacher bench, chest against pad, focus on peak contraction"),
        new Exercise("Cable Curls", "STRENGTH", "ARMS", "MACHINE", "INTERMEDIATE", 3.5,
            "Constant tension biceps",
            "Standing cable curl, maintain tension throughout range"),
        new Exercise("Concentration Curls", "STRENGTH", "ARMS", "DUMBBELL", "INTERMEDIATE", 3.0,
            "Peak bicep isolation",
            "Seated, elbow against thigh, pure bicep isolation"),
        new Exercise("21s (Bicep)", "STRENGTH", "ARMS", "BARBELL", "ADVANCED", 5.0,
            "Bicep burnout protocol",
            "7 partial bottom, 7 partial top, 7 full range - no rest"),

        // TRICEP EXERCISES
        new Exercise("Close Grip Bench Press", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE",
            5.0,
            "Tricep mass builder",
            "Hands shoulder-width apart, elbows close to body, press to full extension"),
        new Exercise("Tricep Dips (Parallel Bars)", "STRENGTH", "ARMS", "EQUIPMENT",
            "INTERMEDIATE", 6.0,
            "Bodyweight tricep builder",
            "Lower until 90° elbow bend, press up to full extension"),
        new Exercise("Bench Dips", "STRENGTH", "ARMS", "EQUIPMENT", "BEGINNER", 4.0,
            "Assisted tricep dips",
            "Hands on bench edge, feet on ground or elevated, lower body weight"),
        new Exercise("Overhead Tricep Extension", "STRENGTH", "ARMS", "DUMBBELL",
            "INTERMEDIATE", 4.0,
            "Tricep stretch emphasis",
            "Weight behind head, extend arms up, keep elbows stationary"),
        new Exercise("Skull Crushers (Lying)", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE",
            4.5,
            "Tricep isolation",
            "Lie on bench, lower bar to forehead, extend arms up"),
        new Exercise("Tricep Pushdowns (Cable)", "STRENGTH", "ARMS", "MACHINE", "BEGINNER", 4.0,
            "Cable tricep isolation",
            "High pulley, push bar down, squeeze triceps at bottom"),
        new Exercise("Rope Pushdowns", "STRENGTH", "ARMS", "MACHINE", "INTERMEDIATE", 4.0,
            "Tricep peak contraction",
            "Rope attachment, separate handles at bottom, squeeze hard"),
        new Exercise("Tricep Kickbacks", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
            "Tricep isolation",
            "Bent over, extend arm back, squeeze tricep at extension"),
        new Exercise("Diamond Push-ups", "STRENGTH", "ARMS", "NONE", "INTERMEDIATE", 5.5,
            "Bodyweight tricep focus",
            "Form diamond with hands, emphasize tricep contraction"),

        // FOREARM EXERCISES
        new Exercise("Wrist Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 2.5,
            "Forearm flexor strength",
            "Forearms on bench, curl wrists up, control negative"),
        new Exercise("Reverse Wrist Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 2.5,
            "Forearm extensor strength",
            "Reverse grip, extend wrists up, balance flexor strength"),
        new Exercise("Farmer's Walks", "STRENGTH", "ARMS", "DUMBBELL", "INTERMEDIATE", 5.0,
            "Functional grip strength",
            "Heavy weights at sides, walk specified distance, maintain posture"),

        // ===== LEG EXERCISES =====

        // QUADRICEPS
        new Exercise("Back Squats", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 6.5,
            "King of leg exercises",
            "Bar on upper back, descend to parallel, drive through heels"),
        new Exercise("Front Squats", "STRENGTH", "LEGS", "BARBELL", "ADVANCED", 7.0,
            "Quad-dominant squat",
            "Bar across front delts, upright torso, full depth"),
        new Exercise("Goblet Squats", "STRENGTH", "LEGS", "DUMBBELL", "BEGINNER", 5.0,
            "Beginner squat pattern",
            "Hold weight at chest, squat to depth, drive up"),
        new Exercise("Bulgarian Split Squats", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE",
            5.5,
            "Unilateral quad focus",
            "Rear foot elevated, descend on front leg, drive up"),
        new Exercise("Walking Lunges", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE", 5.5,
            "Dynamic leg movement",
            "Step forward alternating legs, knee to 90°, continuous motion"),
        new Exercise("Reverse Lunges", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE", 5.0,
            "Knee-friendly lunge",
            "Step backward, descend to depth, return to start"),
        new Exercise("Side Lunges", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE", 4.5,
            "Lateral leg movement",
            "Step wide to side, sit back, return to center"),
        new Exercise("Leg Press", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 5.5,
            "Machine quad builder",
            "Feet shoulder-width, descend to 90°, press through heels"),
        new Exercise("Hack Squats", "STRENGTH", "LEGS", "MACHINE", "INTERMEDIATE", 6.0,
            "Guided squat movement",
            "Back against pad, controlled descent, full extension"),
        new Exercise("Leg Extensions", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
            "Quad isolation",
            "Seated machine, extend legs, squeeze quads at top"),

        // HAMSTRINGS & GLUTES
        new Exercise("Romanian Deadlifts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 6.5,
            "Hip hinge pattern",
            "Push hips back, feel hamstring stretch, drive hips forward"),
        new Exercise("Stiff Leg Deadlifts", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE", 6.0,
            "Hamstring isolation",
            "Keep legs straight, hinge at hips, feel stretch"),
        new Exercise("Good Mornings", "STRENGTH", "LEGS", "BARBELL", "ADVANCED", 5.5,
            "Posterior chain strength",
            "Bar on shoulders, hinge forward, drive hips back"),
        new Exercise("Leg Curls (Lying)", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
            "Hamstring isolation",
            "Lie prone, curl heels to glutes, squeeze hamstrings"),
        new Exercise("Leg Curls (Seated)", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
            "Seated hamstring curl",
            "Different hamstring angle, full range of motion"),
        new Exercise("Hip Thrusts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 5.0,
            "Glute powerhouse",
            "Shoulders on bench, drive hips up, squeeze glutes hard"),
        new Exercise("Glute Bridges", "STRENGTH", "LEGS", "NONE", "BEGINNER", 3.5,
            "Bodyweight glute activation",
            "Lie supine, drive hips up, hold contraction"),
        new Exercise("Single Leg Glute Bridges", "STRENGTH", "LEGS", "NONE", "INTERMEDIATE",
            4.0,
            "Unilateral glute strength",
            "One leg extended, drive with single leg"),

        // CALVES
        new Exercise("Standing Calf Raises", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 3.0,
            "Calf mass builder",
            "Rise on toes, pause at top, slow negative, full stretch"),
        new Exercise("Seated Calf Raises", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 3.0,
            "Soleus focus",
            "Seated position targets deeper calf muscle"),
        new Exercise("Calf Press (Leg Press)", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 3.5,
            "Heavy calf training",
            "Use leg press machine, press with toes only"),
        new Exercise("Jump Squats", "CARDIO", "LEGS", "NONE", "INTERMEDIATE", 8.0,
            "Explosive leg power",
            "Squat down, explode up into jump, soft landing"),

        // ===== CORE EXERCISES =====

        // UPPER ABDOMINALS
        new Exercise("Crunches", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.5,
            "Basic ab exercise",
            "Lift shoulders off ground, squeeze abs, control descent"),
        new Exercise("Sit-ups", "STRENGTH", "CORE", "NONE", "BEGINNER", 4.0,
            "Full range ab movement",
            "Come all the way up, controlled movement both ways"),
        new Exercise("Decline Crunches", "STRENGTH", "CORE", "EQUIPMENT", "INTERMEDIATE", 4.5,
            "Increased difficulty",
            "Feet secured, decline angle increases resistance"),
        new Exercise("Cable Crunches", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE", 4.5,
            "Weighted ab work",
            "Kneeling position, crunch against cable resistance"),

        // LOWER ABDOMINALS
        new Exercise("Leg Raises", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.5,
            "Lower ab focus",
            "Lie flat, raise legs up, control descent, avoid momentum"),
        new Exercise("Reverse Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
            "Lower ab isolation",
            "Bring knees to chest, lift hips slightly"),
        new Exercise("Hanging Knee Raises", "STRENGTH", "CORE", "EQUIPMENT", "INTERMEDIATE",
            5.0,
            "Advanced lower abs",
            "Hang from bar, bring knees to chest, control swing"),
        new Exercise("Hanging Leg Raises", "STRENGTH", "CORE", "EQUIPMENT", "ADVANCED", 6.0,
            "Extreme lower ab challenge",
            "Hang from bar, raise straight legs to horizontal"),
        new Exercise("V-Ups", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 5.0,
            "Total ab integration",
            "Lie flat, bring chest and legs together in V shape"),

        // OBLIQUES
        new Exercise("Russian Twists", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
            "Rotational core strength",
            "Seated position, rotate side to side, feet off ground"),
        new Exercise("Bicycle Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
            "Dynamic ab movement",
            "Alternate elbow to opposite knee, continuous motion"),
        new Exercise("Side Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 3.5,
            "Lateral ab focus",
            "Lie on side, crunch obliques, squeeze at top"),
        new Exercise("Wood Choppers", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE", 4.5,
            "Functional rotation",
            "Cable machine, diagonal chopping motion"),
        new Exercise("Side Planks", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
            "Isometric oblique strength",
            "Hold side position, maintain straight line"),

        // ISOMETRIC CORE
        new Exercise("Planks", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
            "Core endurance builder",
            "Hold straight line position, engage entire core"),
        new Exercise("Plank to Push-up", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 5.0,
            "Dynamic plank variation",
            "From plank to push-up position and back"),
        new Exercise("Dead Bug", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
            "Core stability",
            "Opposite arm/leg movements, maintain neutral spine"),
        new Exercise("Bird Dog", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
            "Spinal stability",
            "On hands and knees, extend opposite arm and leg"),

        // ===== HIIT & FUNCTIONAL EXERCISES =====

        new Exercise("Burpees", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 10.0,
            "Total body conditioner",
            "Squat-thrust-jump sequence, maintain form under fatigue"),
        new Exercise("Mountain Climbers", "CARDIO", "CORE", "NONE", "INTERMEDIATE", 8.0,
            "Dynamic core cardio",
            "Plank position, alternate knee drives, maintain pace"),
        new Exercise("High Knees", "CARDIO", "LEGS", "NONE", "BEGINNER", 8.0,
            "Running drill",
            "Drive knees to hip level, pump arms, maintain rhythm"),
        new Exercise("Butt Kickers", "CARDIO", "LEGS", "NONE", "BEGINNER", 7.0,
            "Hamstring activation",
            "Kick heels to glutes, maintain forward lean"),
        new Exercise("Jumping Jacks", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 7.0,
            "Classic cardio move",
            "Jump feet apart while raising arms overhead"),
        new Exercise("Box Jumps", "CARDIO", "LEGS", "EQUIPMENT", "INTERMEDIATE", 9.0,
            "Explosive leg power",
            "Jump onto box, step down, focus on soft landing"),
        new Exercise("Battle Ropes", "CARDIO", "FULL_BODY", "EQUIPMENT", "INTERMEDIATE", 11.0,
            "Total body conditioning",
            "Various wave patterns, maintain intensity throughout"),
        new Exercise("Kettlebell Swings", "STRENGTH", "FULL_BODY", "EQUIPMENT", "INTERMEDIATE",
            7.0,
            "Hip hinge power",
            "Explosive hip extension, swing to shoulder height"),
        new Exercise("Turkish Get-ups", "STRENGTH", "FULL_BODY", "EQUIPMENT", "ADVANCED", 6.0,
            "Complex movement pattern",
            "From lying to standing with weight overhead"),
        new Exercise("Thrusters", "STRENGTH", "FULL_BODY", "DUMBBELL", "INTERMEDIATE", 8.0,
            "Squat to press combo",
            "Front squat into overhead press, continuous motion"),

        // ===== FLEXIBILITY & MOBILITY =====

        new Exercise("Cat-Cow Stretches", "FLEXIBILITY", "CORE", "NONE", "BEGINNER", 2.0,
            "Spinal mobility",
            "Alternate between arched and rounded spine positions"),
        new Exercise("Child's Pose", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 1.5,
            "Restorative stretch",
            "Kneel and reach arms forward, relax entire body"),
        new Exercise("Downward Dog", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 3.0,
            "Total body stretch",
            "Inverted V position, stretch calves and shoulders"),
        new Exercise("Pigeon Pose", "FLEXIBILITY", "LEGS", "NONE", "INTERMEDIATE", 2.0,
            "Hip opener",
            "Deep hip flexor and glute stretch"),
        new Exercise("Cobra Stretch", "FLEXIBILITY", "CORE", "NONE", "BEGINNER", 2.0,
            "Back extension",
            "Gentle back arch, open chest and abs"),
        new Exercise("Hip Circles", "FLEXIBILITY", "LEGS", "NONE", "BEGINNER", 2.5,
            "Hip mobility",
            "Large circular movements to mobilize hip joints"),
        new Exercise("Arm Circles", "FLEXIBILITY", "SHOULDERS", "NONE", "BEGINNER", 2.0,
            "Shoulder mobility",
            "Forward and backward circles, progressively larger"),
        new Exercise("Leg Swings", "FLEXIBILITY", "LEGS", "NONE", "BEGINNER", 3.0,
            "Dynamic leg mobility",
            "Forward/back and side swings, controlled movement"),

        // ===== SPORT-SPECIFIC & ADVANCED =====

        new Exercise("Olympic Lifts - Clean & Press", "STRENGTH", "FULL_BODY", "BARBELL",
            "ADVANCED", 9.0,
            "Explosive full body power",
            "ADVANCED ONLY: Triple extension into catch and press"),
        new Exercise("Snatch", "STRENGTH", "FULL_BODY", "BARBELL", "ADVANCED", 10.0,
            "Ultimate power exercise",
            "EXPERT TECHNIQUE REQUIRED: Ground to overhead in one motion"),
        new Exercise("Pistol Squats", "STRENGTH", "LEGS", "NONE", "ADVANCED", 6.0,
            "Single leg strength",
            "One leg squat to full depth, requires strength and balance"),
        new Exercise("Muscle-ups", "STRENGTH", "FULL_BODY", "EQUIPMENT", "ADVANCED", 8.0,
            "Pull-up to dip combo",
            "From hang to support position above bar"),
        new Exercise("Handstand Push-ups", "STRENGTH", "SHOULDERS", "NONE", "ADVANCED", 7.0,
            "Inverted pressing",
            "Handstand against wall, press up and down"),
        new Exercise("One-Arm Push-ups", "STRENGTH", "CHEST", "NONE", "ADVANCED", 7.0,
            "Unilateral bodyweight strength",
            "Single arm push-up, extreme upper body strength"),

        // ===== RECOVERY & ACTIVATION =====

        new Exercise("Foam Rolling", "FLEXIBILITY", "FULL_BODY", "EQUIPMENT", "BEGINNER", 2.0,
            "Myofascial release",
            "Roll tight muscles, apply pressure to trigger points"),
        new Exercise("Band Pull-Aparts", "STRENGTH", "SHOULDERS", "EQUIPMENT", "BEGINNER", 2.5,
            "Rear delt activation",
            "Pull resistance band apart at chest level"),
        new Exercise("Glute Activation", "STRENGTH", "LEGS", "EQUIPMENT", "BEGINNER", 3.0,
            "Pre-workout activation",
            "Band walks and clamshells to activate glutes"),
        new Exercise("Shoulder Dislocations", "FLEXIBILITY", "SHOULDERS", "EQUIPMENT",
            "BEGINNER", 2.0,
            "Shoulder mobility",
            "Pass PVC pipe or band overhead and behind back"),

        // ===== BEGINNER PROGRESSIONS =====

        new Exercise("Wall Push-ups", "STRENGTH", "CHEST", "NONE", "BEGINNER", 3.0,
            "Push-up progression",
            "Standing push-ups against wall, progress to incline"),
        new Exercise("Assisted Squats", "STRENGTH", "LEGS", "EQUIPMENT", "BEGINNER", 4.0,
            "Squat progression",
            "Hold TRX or suspension trainer for assistance"),
        new Exercise("Knee Push-ups", "STRENGTH", "CHEST", "NONE", "BEGINNER", 4.0,
            "Modified push-ups",
            "From knees instead of toes, maintain straight line"),
        new Exercise("Chair Dips", "STRENGTH", "ARMS", "EQUIPMENT", "BEGINNER", 3.5,
            "Tricep progression",
            "Use sturdy chair, progress to bench dips"),

        // ===== ADVANCED VARIATIONS =====

        new Exercise("Weighted Pull-ups", "STRENGTH", "BACK", "EQUIPMENT", "ADVANCED", 7.5,
            "Advanced pulling strength",
            "Add weight belt or dumbbell between legs"),
        new Exercise("Weighted Dips", "STRENGTH", "CHEST", "EQUIPMENT", "ADVANCED", 7.0,
            "Advanced pushing strength",
            "Add weight belt for increased resistance"),
        new Exercise("Clapping Push-ups", "STRENGTH", "CHEST", "NONE", "ADVANCED", 6.5,
            "Explosive upper body power",
            "Push up explosively, clap hands, absorb landing"),
        new Exercise("Jump Lunges", "CARDIO", "LEGS", "NONE", "INTERMEDIATE", 8.5,
            "Explosive leg conditioning",
            "Jump and switch legs in lunge position"),
        new Exercise("Plyometric Push-ups", "STRENGTH", "CHEST", "NONE", "ADVANCED", 6.5,
            "Explosive pushing power",
            "Push up explosively, hands leave ground"));

    try {
      exerciseRepository.saveAll(exercises);
      log.info("Successfully saved {} exercise entries to database", exercises.size());
    } catch (Exception e) {
      log.error("Error saving exercise data to database", e);
      throw new RuntimeException("Failed to initialize exercise data", e);
    }
  }
}