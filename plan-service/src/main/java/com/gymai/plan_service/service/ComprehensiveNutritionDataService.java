// package com.gymai.plan_service.service;

// import java.util.Arrays;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.springframework.stereotype.Service;

// import com.gymai.plan_service.entity.Exercise;
// import com.gymai.plan_service.entity.Food;
// import com.gymai.plan_service.repository.ExerciseRepository;
// import com.gymai.plan_service.repository.FoodRepository;

// import jakarta.annotation.PostConstruct;

// /**
// * Comprehensive Nutrition & Exercise Data Service
// * Based on Guru Mann's Professional Fitness Programs:
// * - 6 Week Shredded
// * - Mass-Up Program
// * - Muscular 8
// * - Muscle Mode
// * - Diabetes & Cholesterol Management Plans
// * - Hypertension Control Program
// */
// @Service
// @Component
// public class ComprehensiveNutritionDataService {

// @Autowired
// private FoodRepository foodRepository;

// @Autowired
// private ExerciseRepository exerciseRepository;

// @PostConstruct
// public void initializeData() {
// if (foodRepository.count() == 0) {
// initializeComprehensiveFoodData();
// }

// if (exerciseRepository.count() == 0) {
// initializeComprehensiveExerciseData();
// }
// }

// private void initializeComprehensiveFoodData() {
// List<Food> foods = Arrays.asList(
// // ===== BREAKFAST FOODS =====

// // GRAINS & CEREALS
// new Food("Oats (1 cup)", 389, 16.9, 66.3, 6.9, 10.6, "VEG", "BREAKFAST",
// "GRAINS"),
// new Food("Muscle Oats (Guru Mann Recipe)", 595, 35.0, 80.0, 15.0, 12.0,
// "VEG",
// "BREAKFAST", "GRAINS"),
// new Food("Recovery Oats", 595, 35.0, 46.0, 10.0, 8.0, "VEG", "BREAKFAST",
// "GRAINS"),
// new Food("Muesli (1 cup)", 352, 9.7, 66.0, 5.9, 7.3, "VEG", "BREAKFAST",
// "GRAINS"),
// new Food("Whole Grain Cereals", 379, 13.0, 67.0, 6.0, 10.0, "VEG",
// "BREAKFAST",
// "GRAINS"),
// new Food("Daliya (Broken Wheat)", 342, 12.0, 69.0, 1.8, 12.5, "VEG",
// "BREAKFAST",
// "GRAINS"),
// new Food("Multi Grain Mix (Guru Mann)", 410, 30.0, 50.0, 10.0, 8.0, "VEG",
// "BREAKFAST",
// "GRAINS"),
// new Food("Quinoa (cooked)", 222, 8.0, 39.0, 3.6, 5.2, "VEG", "BREAKFAST",
// "GRAINS"),

// // BREAKFAST BREADS
// new Food("Whole Wheat Bread (1 slice)", 82, 4.2, 13.7, 1.1, 2.5, "VEG",
// "BREAKFAST",
// "GRAINS"),
// new Food("Whole Wheat Bread (3 slices)", 247, 12.6, 41.0, 3.4, 7.4, "VEG",
// "BREAKFAST",
// "GRAINS"),
// new Food("Banana Bread Toast (Guru Mann)", 500, 28.0, 62.0, 15.0, 7.0, "VEG",
// "BREAKFAST", "GRAINS"),

// // BREAKFAST PROTEINS
// new Food("Eggs (1 whole)", 68, 5.5, 0.4, 4.4, 0, "VEG", "BREAKFAST",
// "PROTEIN"),
// new Food("Egg Whites (1 white)", 17, 3.6, 0.2, 0.1, 0, "VEG", "BREAKFAST",
// "PROTEIN"),
// new Food("Scrambled Eggs (6 whites)", 102, 21.6, 1.2, 0.6, 0, "VEG",
// "BREAKFAST",
// "PROTEIN"),
// new Food("Vegetable Omelet", 392, 36.0, 35.0, 12.0, 3.0, "VEG", "BREAKFAST",
// "PROTEIN"),
// new Food("Spinach Omelet (Guru Mann)", 410, 30.0, 50.0, 10.0, 4.0, "VEG",
// "BREAKFAST",
// "PROTEIN"),

// // BREAKFAST DAIRY & ALTERNATIVES
// new Food("Non Fat Milk (1 cup)", 42, 3.4, 5.0, 0.2, 0, "VEG", "BREAKFAST",
// "DAIRY"),
// new Food("Low Fat Milk (1 glass)", 102, 8.1, 12.2, 2.4, 0, "VEG",
// "BREAKFAST", "DAIRY"),
// new Food("Greek Yogurt", 59, 10.3, 3.6, 0.4, 0, "VEG", "BREAKFAST", "DAIRY"),
// new Food("Almond Milk", 17, 0.6, 1.5, 1.2, 0.2, "VEGAN", "BREAKFAST",
// "DAIRY"),
// new Food("Soy Milk", 54, 3.3, 6.3, 1.8, 0.6, "VEGAN", "BREAKFAST", "DAIRY"),
// new Food("Protein Smoothie (Guru Mann)", 410, 30.0, 50.0, 10.0, 3.0, "VEG",
// "BREAKFAST",
// "PROTEIN"),

// // BREAKFAST SUPPLEMENTS
// new Food("Whey Protein (1 scoop)", 120, 24.0, 2.0, 1.0, 0, "VEG",
// "BREAKFAST",
// "SUPPLEMENTS"),
// new Food("Protinex (1 scoop)", 90, 18.0, 4.0, 0.5, 0, "VEG", "BREAKFAST",
// "SUPPLEMENTS"),

// // ===== SNACK FOODS =====

// // MORNING SNACKS
// new Food("Apple with Peanut Butter", 320, 8.0, 35.0, 16.0, 6.0, "VEG",
// "SNACK",
// "FRUITS"),
// new Food("Protein Cupcake (Guru Mann)", 295, 34.0, 6.0, 15.0, 2.0, "VEG",
// "SNACK",
// "PROTEIN"),
// new Food("Banana Smoothie", 250, 8.0, 45.0, 6.0, 4.0, "VEG", "SNACK",
// "FRUITS"),

// // NUTS & SEEDS
// new Food("Almonds (28g)", 164, 6.0, 6.0, 14.0, 3.5, "VEG", "SNACK", "NUTS"),
// new Food("Cashews (28g)", 157, 5.2, 8.6, 12.4, 0.9, "VEG", "SNACK", "NUTS"),
// new Food("Walnuts (28g)", 185, 4.3, 3.9, 18.5, 1.9, "VEG", "SNACK", "NUTS"),
// new Food("Peanuts (28g)", 161, 7.3, 4.6, 14.0, 2.4, "VEG", "SNACK", "NUTS"),
// new Food("Peanut Butter (1 tbsp)", 94, 4.0, 3.1, 8.1, 0.9, "VEG", "SNACK",
// "NUTS"),
// new Food("Almond Butter (1 tbsp)", 98, 3.7, 3.2, 9.0, 1.6, "VEG", "SNACK",
// "NUTS"),

// // FRUITS
// new Food("Apple (1 medium)", 52, 0.3, 13.8, 0.2, 2.4, "VEG", "SNACK",
// "FRUITS"),
// new Food("Banana (1 medium)", 89, 1.1, 22.8, 0.3, 2.6, "VEG", "SNACK",
// "FRUITS"),
// new Food("Orange (1 medium)", 47, 0.9, 11.8, 0.1, 2.4, "VEG", "SNACK",
// "FRUITS"),
// new Food("Pear (1 medium)", 57, 0.4, 15.2, 0.1, 3.1, "VEG", "SNACK",
// "FRUITS"),
// new Food("Strawberries (1 cup)", 49, 1.0, 11.7, 0.5, 3.0, "VEG", "SNACK",
// "FRUITS"),
// new Food("Blueberries (1 cup)", 84, 1.1, 21.5, 0.5, 3.6, "VEG", "SNACK",
// "FRUITS"),
// new Food("Papaya (1 cup)", 55, 0.9, 13.7, 0.2, 2.5, "VEG", "SNACK",
// "FRUITS"),
// new Food("Guava (1 medium)", 112, 4.2, 23.6, 1.6, 8.9, "VEG", "SNACK",
// "FRUITS"),
// new Food("Black Grapes (1 cup)", 104, 1.1, 27.3, 0.2, 1.4, "VEG", "SNACK",
// "FRUITS"),
// new Food("Pomegranate (1 cup)", 134, 2.9, 30.0, 1.9, 6.4, "VEG", "SNACK",
// "FRUITS"),

// // HEALTHY SNACKS
// new Food("Roasted Chickpeas (1 cup)", 269, 12.1, 45.0, 4.3, 9.9, "VEG",
// "SNACK",
// "PROTEIN"),
// new Food("Roasted Soy Beans", 387, 34.0, 28.1, 18.6, 7.0, "VEGAN", "SNACK",
// "PROTEIN"),
// new Food("Protein Laddoo (Guru Mann)", 320, 25.0, 15.0, 18.0, 3.0, "VEG",
// "SNACK",
// "PROTEIN"),
// new Food("Fresh Juice with Channa", 401, 34.0, 46.0, 9.0, 8.0, "VEG",
// "SNACK",
// "FRUITS"),

// // ===== LUNCH FOODS =====

// // RICE & GRAINS
// new Food("Brown Rice (cooked, 1 cup)", 216, 5.0, 45.0, 1.8, 3.5, "VEG",
// "LUNCH",
// "GRAINS"),
// new Food("Super Rice (Guru Mann)", 500, 32.0, 56.0, 16.0, 2.0, "VEG",
// "LUNCH",
// "GRAINS"),
// new Food("Sweet Muscle Rice", 597, 33.0, 78.0, 17.0, 4.0, "VEG", "LUNCH",
// "GRAINS"),
// new Food("Recovery Rice", 268, 20.0, 38.0, 4.0, 3.0, "VEG", "LUNCH",
// "GRAINS"),
// new Food("Rice Palao", 320, 12.0, 55.0, 8.0, 4.0, "VEG", "LUNCH", "GRAINS"),

// // ROTIS & BREADS
// new Food("Whole Wheat Roti (1 medium)", 71, 2.7, 15.2, 0.4, 2.0, "VEG",
// "LUNCH",
// "GRAINS"),
// new Food("Chapati (1 medium)", 71, 2.7, 15.2, 0.4, 2.0, "VEG", "LUNCH",
// "GRAINS"),
// new Food("Super Roti (Guru Mann)", 494, 32.0, 51.0, 18.0, 8.0, "VEG",
// "LUNCH",
// "GRAINS"),

// // LEGUMES & BEANS
// new Food("Lentils (Dal, cooked)", 116, 9.0, 20.1, 0.4, 7.9, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Mixed Lentils", 120, 9.5, 20.5, 0.5, 8.0, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Black Lentils", 341, 25.2, 58.9, 1.6, 18.3, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Kidney Beans (Rajma)", 127, 8.7, 22.8, 0.5, 6.4, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Black Kidney Beans", 132, 8.9, 23.7, 0.5, 8.7, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Chickpeas (cooked)", 164, 8.9, 27.4, 2.6, 7.6, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Black Chickpeas", 378, 20.1, 61.0, 5.3, 17.1, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("White Chickpeas", 378, 20.1, 61.0, 5.3, 17.1, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Black Beans", 132, 8.9, 23.7, 0.5, 8.7, "VEGAN", "LUNCH",
// "PROTEIN"),

// // VEGETABLES
// new Food("Spinach (cooked)", 23, 2.9, 3.6, 0.4, 2.2, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Broccoli (cooked)", 34, 2.8, 6.6, 0.4, 2.6, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Cauliflower (cooked)", 25, 1.9, 5.0, 0.3, 2.0, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Green Bell Pepper", 20, 0.9, 4.6, 0.2, 1.7, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Red Bell Pepper", 31, 1.0, 7.3, 0.3, 2.5, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Green Beans", 35, 1.8, 8.0, 0.1, 2.7, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Green Peas", 81, 5.4, 14.5, 0.4, 5.7, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Sweet Potato (cooked)", 86, 1.6, 20.1, 0.1, 3.0, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Carrot (cooked)", 41, 0.9, 9.6, 0.2, 2.8, "VEG", "LUNCH",
// "VEGETABLES"),
// new Food("Cabbage", 25, 1.3, 5.8, 0.1, 2.5, "VEG", "LUNCH", "VEGETABLES"),
// new Food("Onion", 40, 1.1, 9.3, 0.1, 1.7, "VEG", "LUNCH", "VEGETABLES"),
// new Food("Tomato", 18, 0.9, 3.9, 0.2, 1.2, "VEG", "LUNCH", "VEGETABLES"),
// new Food("Cucumber", 16, 0.7, 3.6, 0.1, 0.5, "VEG", "LUNCH", "VEGETABLES"),
// new Food("Mushrooms", 22, 3.1, 3.3, 0.3, 1.0, "VEG", "LUNCH", "VEGETABLES"),

// // PROTEIN SOURCES
// new Food("Paneer (100g)", 265, 18.3, 1.2, 20.8, 0, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Fresh Paneer", 265, 18.3, 1.2, 20.8, 0, "VEG", "LUNCH", "PROTEIN"),
// new Food("Low Fat Paneer", 206, 20.0, 3.0, 12.0, 0, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Grilled Paneer", 265, 18.3, 1.2, 20.8, 0, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Tofu (100g)", 76, 8.1, 1.9, 4.8, 0.3, "VEGAN", "LUNCH", "PROTEIN"),
// new Food("Soy Chunks (50g)", 173, 26.0, 16.5, 0.3, 6.5, "VEGAN", "LUNCH",
// "PROTEIN"),
// new Food("Tempeh (100g)", 193, 19.0, 9.4, 11.0, 9.0, "VEGAN", "LUNCH",
// "PROTEIN"),

// // NON-VEG PROTEINS
// new Food("Grilled Chicken (100g)", 165, 31.0, 0, 3.6, 0, "NON_VEG", "LUNCH",
// "PROTEIN"),
// new Food("Chicken Breast (100g)", 165, 31.0, 0, 3.6, 0, "NON_VEG", "LUNCH",
// "PROTEIN"),
// new Food("Chicken Curry", 200, 25.0, 8.0, 8.0, 1.0, "NON_VEG", "LUNCH",
// "PROTEIN"),
// new Food("Tandoori Chicken", 150, 28.0, 5.0, 2.5, 0, "NON_VEG", "LUNCH",
// "PROTEIN"),
// new Food("Chicken Drumstick (2 pieces)", 172, 28.3, 0, 5.7, 0, "NON_VEG",
// "LUNCH",
// "PROTEIN"),
// new Food("Fish (100g)", 206, 22.0, 0, 12.0, 0, "NON_VEG", "LUNCH",
// "PROTEIN"),
// new Food("Salmon (100g)", 208, 22.0, 0, 12.4, 0, "NON_VEG", "LUNCH",
// "PROTEIN"),

// // SPECIALTY LUNCH DISHES
// new Food("Muscle Builder Potato Bowl", 490, 32.0, 68.0, 10.0, 8.0, "VEG",
// "LUNCH",
// "MIXED"),
// new Food("Potato Bean Bowl (Guru Mann)", 410, 30.0, 50.0, 10.0, 8.0, "VEG",
// "LUNCH",
// "MIXED"),
// new Food("Bean Bowl", 350, 20.0, 45.0, 12.0, 12.0, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Paneer Sandwich", 325, 30.0, 40.0, 5.0, 4.0, "VEG", "LUNCH",
// "MIXED"),

// // ===== DINNER FOODS =====

// // DINNER MAINS
// new Food("Roti Roll (Guru Mann)", 343, 20.0, 32.0, 15.0, 6.0, "VEG",
// "DINNER", "MIXED"),
// new Food("Omelet Beans Wrap", 450, 35.0, 35.0, 20.0, 8.0, "VEG", "DINNER",
// "MIXED"),
// new Food("Roti Beans Wrap", 410, 30.0, 45.0, 15.0, 10.0, "VEG", "DINNER",
// "MIXED"),
// new Food("Paneer Bhurji with Roti", 404, 30.0, 44.0, 12.0, 4.0, "VEG",
// "DINNER",
// "MIXED"),
// new Food("Boiled Egg Sabji", 410, 30.0, 50.0, 10.0, 4.0, "VEG", "DINNER",
// "PROTEIN"),
// new Food("Paneer Sabji", 410, 25.0, 55.0, 10.0, 4.0, "VEG", "DINNER",
// "PROTEIN"),

// // DINNER SALADS
// new Food("Broccoli Cabbage Salad", 45, 3.0, 8.0, 0.5, 4.0, "VEG", "DINNER",
// "VEGETABLES"),
// new Food("Beans Salad", 120, 8.0, 18.0, 2.0, 6.0, "VEG", "DINNER",
// "VEGETABLES"),
// new Food("Mixed Vegetable Salad", 50, 2.5, 10.0, 0.5, 4.0, "VEG", "DINNER",
// "VEGETABLES"),
// new Food("Cucumber Salad", 20, 1.0, 4.0, 0.1, 1.5, "VEG", "DINNER",
// "VEGETABLES"),

// // DINNER SOUPS
// new Food("Tomato Soup (homemade)", 75, 2.0, 16.0, 1.0, 3.0, "VEG", "DINNER",
// "VEGETABLES"),
// new Food("Chicken Soup", 120, 12.0, 8.0, 4.0, 1.0, "NON_VEG", "DINNER",
// "PROTEIN"),
// new Food("Vegetable Soup", 60, 2.5, 12.0, 0.5, 3.0, "VEG", "DINNER",
// "VEGETABLES"),

// // LIGHT DINNER OPTIONS
// new Food("Mixed Lentil with Rice", 350, 15.0, 60.0, 6.0, 10.0, "VEG",
// "DINNER",
// "MIXED"),
// new Food("Dal Chawal", 300, 12.0, 55.0, 5.0, 8.0, "VEG", "DINNER", "MIXED"),
// new Food("Quinoa Salad", 280, 8.0, 45.0, 8.0, 5.0, "VEG", "DINNER", "MIXED"),

// // ===== PRE/POST WORKOUT =====

// // PRE-WORKOUT
// new Food("Black Coffee", 2, 0.3, 0, 0, 0, "VEG", "PRE_WORKOUT", "BEVERAGES"),
// new Food("Green Tea", 1, 0, 0, 0, 0, "VEG", "PRE_WORKOUT", "BEVERAGES"),
// new Food("Pre-Workout Drink", 60, 0, 15.0, 0, 0, "VEG", "PRE_WORKOUT",
// "SUPPLEMENTS"),

// // POST-WORKOUT
// new Food("Post Workout Shake", 499, 40.0, 78.0, 3.0, 0, "VEG",
// "POST_WORKOUT",
// "PROTEIN"),
// new Food("Whey Protein Shake", 258, 36.0, 24.0, 3.0, 0, "VEG",
// "POST_WORKOUT",
// "PROTEIN"),
// new Food("Protein Shake (Simple)", 400, 30.0, 70.0, 0, 0, "VEG",
// "POST_WORKOUT",
// "PROTEIN"),
// new Food("Recovery Drink", 308, 25.0, 52.0, 0, 0, "VEG", "POST_WORKOUT",
// "PROTEIN"),

// // ===== BEFORE BED =====

// new Food("Protein Burfi", 279, 25.0, 11.0, 15.0, 2.0, "VEG", "BEFORE_BED",
// "PROTEIN"),
// new Food("Almond Milk (warm)", 210, 16.0, 18.0, 8.0, 1.0, "VEG",
// "BEFORE_BED", "DAIRY"),
// new Food("Protein Pancake", 375, 30.0, 30.0, 15.0, 3.0, "VEG", "BEFORE_BED",
// "PROTEIN"),
// new Food("Rice Cake with Peanut Butter", 500, 36.0, 30.0, 15.0, 2.0, "VEG",
// "BEFORE_BED", "MIXED"),
// new Food("Casein Protein", 110, 24.0, 3.0, 0.5, 0, "VEG", "BEFORE_BED",
// "SUPPLEMENTS"),

// // ===== COOKING INGREDIENTS & CONDIMENTS =====

// new Food("Olive Oil (1 tbsp)", 119, 0, 0, 13.5, 0, "VEG", "COOKING", "FATS"),
// new Food("Coconut Oil (1 tbsp)", 117, 0, 0, 14.0, 0, "VEGAN", "COOKING",
// "FATS"),
// new Food("Flaxseed Oil (1 tbsp)", 120, 0, 0, 13.6, 0, "VEGAN", "COOKING",
// "FATS"),
// new Food("Honey (1 tbsp)", 64, 0.1, 17.3, 0, 0.1, "VEG", "COOKING",
// "CONDIMENTS"),
// new Food("Stevia", 0, 0, 0, 0, 0, "VEG", "COOKING", "CONDIMENTS"),
// new Food("Cinnamon (1 tsp)", 6, 0.1, 2.1, 0.1, 1.4, "VEG", "COOKING",
// "SPICES"),
// new Food("Ginger", 4, 0.1, 0.9, 0.1, 0.1, "VEG", "COOKING", "SPICES"),
// new Food("Garlic (1 clove)", 4, 0.2, 1.0, 0, 0.1, "VEG", "COOKING",
// "SPICES"),
// new Food("Ketchup (1 tbsp)", 19, 0.3, 4.7, 0.1, 0.1, "VEG", "COOKING",
// "CONDIMENTS"),
// new Food("Ranch Dressing (1 tbsp)", 73, 0.1, 1.4, 7.7, 0, "VEG", "COOKING",
// "CONDIMENTS"),
// new Food("Lemon Juice (1 tbsp)", 4, 0.1, 1.3, 0, 0.1, "VEG", "COOKING",
// "FRUITS"),

// // ===== BEVERAGES =====

// new Food("Indian Tea (1 cup)", 1, 0, 0.7, 0, 0, "VEG", "BEVERAGES",
// "BEVERAGES"),
// new Food("Herbal Tea", 1, 0, 0, 0, 0, "VEG", "BEVERAGES", "BEVERAGES"),
// new Food("Gatorade (240ml)", 50, 0, 14.0, 0, 0, "VEG", "BEVERAGES",
// "BEVERAGES"),
// new Food("Water", 0, 0, 0, 0, 0, "VEG", "BEVERAGES", "BEVERAGES"),

// // ===== SPECIAL DIETARY NEEDS =====

// // LOW SODIUM (for Hypertension)
// new Food("Low Sodium Quinoa Bowl", 280, 12.0, 45.0, 8.0, 6.0, "VEG", "LUNCH",
// "GRAINS"),
// new Food("Herb Seasoned Vegetables", 40, 2.0, 8.0, 0.5, 3.0, "VEG", "LUNCH",
// "VEGETABLES"),

// // LOW GLYCEMIC (for Diabetes)
// new Food("Diabetes-Friendly Oats", 150, 8.0, 25.0, 3.0, 4.0, "VEG",
// "BREAKFAST",
// "GRAINS"),
// new Food("Low GI Fruit Bowl", 80, 1.0, 20.0, 0.3, 4.0, "VEG", "SNACK",
// "FRUITS"),

// // HIGH FIBER (for Cholesterol)
// new Food("High Fiber Bean Mix", 180, 12.0, 32.0, 1.0, 15.0, "VEG", "LUNCH",
// "PROTEIN"),
// new Food("Cholesterol-Friendly Salad", 60, 3.0, 12.0, 2.0, 8.0, "VEG",
// "LUNCH",
// "VEGETABLES"),

// // ===== MASS GAIN SPECIALTIES =====

// new Food("Mass Gain Shake (Homemade)", 500, 37.0, 53.0, 16.0, 3.0, "VEG",
// "SNACK",
// "PROTEIN"),
// new Food("Muscle Meat (Guru Mann)", 513, 30.0, 60.0, 17.0, 4.0, "NON_VEG",
// "DINNER",
// "PROTEIN"),
// new Food("Power Snack", 536, 30.0, 68.0, 16.0, 5.0, "VEG", "SNACK", "MIXED"),

// // ===== DRIED FRUITS & OTHERS =====

// new Food("Raisins (1/4 cup)", 109, 1.0, 29.0, 0.2, 1.4, "VEG", "SNACK",
// "FRUITS"),
// new Food("Dates (3 pieces)", 67, 0.4, 18.0, 0.1, 1.6, "VEG", "SNACK",
// "FRUITS"),
// new Food("Cranberries (dried, 1/8 cup)", 92, 0.1, 25.0, 0.4, 1.6, "VEG",
// "SNACK",
// "FRUITS"),

// // ===== SUPPLEMENTS & SPECIAL ITEMS =====

// new Food("Multivitamin", 5, 0, 1.0, 0, 0, "VEG", "SUPPLEMENTS",
// "SUPPLEMENTS"),
// new Food("BCAA (5g)", 10, 2.5, 0, 0, 0, "VEG", "SUPPLEMENTS", "SUPPLEMENTS"),
// new Food("Creatine (5g)", 0, 0, 0, 0, 0, "VEG", "SUPPLEMENTS",
// "SUPPLEMENTS"),
// new Food("Fish Oil (1g)", 9, 0, 0, 1.0, 0, "NON_VEG", "SUPPLEMENTS",
// "SUPPLEMENTS"),
// new Food("Flaxseed Oil Capsule", 9, 0, 0, 1.0, 0, "VEGAN", "SUPPLEMENTS",
// "SUPPLEMENTS"),
// new Food("Carbo Plus (2-3 tbsp)", 240, 0, 60.0, 0, 0, "VEG", "SUPPLEMENTS",
// "SUPPLEMENTS"),
// new Food("Gatorade/Glucose (240ml)", 60, 0, 15.0, 0, 0, "VEG", "SUPPLEMENTS",
// "BEVERAGES"));

// foodRepository.saveAll(foods);
// }

// private void initializeComprehensiveExerciseData() {
// List<Exercise> exercises = Arrays.asList(
// // ===== CARDIO EXERCISES =====

// // RUNNING & JOGGING
// new Exercise("Running (10-12 kmph)", "CARDIO", "FULL_BODY", "NONE",
// "INTERMEDIATE",
// 12.0,
// "High-intensity running",
// "Maintain 10-12 kmph pace, 20-30 minutes duration"),
// new Exercise("Treadmill Running", "CARDIO", "FULL_BODY", "MACHINE",
// "BEGINNER", 10.0,
// "Treadmill running workout",
// "Start with 5min warmup at 2.8-3.0 mph, maintain steady pace"),
// new Exercise("HIIT Treadmill", "CARDIO", "FULL_BODY", "MACHINE", "ADVANCED",
// 15.0,
// "High Intensity Interval Training",
// "30sec sprint/30sec rest, progressive speed increase"),
// new Exercise("Outdoor Walking", "CARDIO", "FULL_BODY", "NONE", "BEGINNER",
// 4.0,
// "Brisk outdoor walking", "Maintain 3-4 mph pace, natural arm swing"),
// new Exercise("Incline Walking", "CARDIO", "FULL_BODY", "MACHINE", "BEGINNER",
// 6.0,
// "Treadmill incline walking", "Level 10-12 incline, moderate pace"),

// // CYCLING
// new Exercise("Stationary Bike", "CARDIO", "LEGS", "MACHINE", "BEGINNER", 7.0,
// "Indoor cycling workout", "Level 10-12 resistance, 20-30 minutes"),
// new Exercise("Cycling", "CARDIO", "LEGS", "NONE", "INTERMEDIATE", 8.0,
// "Outdoor cycling", "Maintain steady pace, adjust for terrain"),

// // OTHER CARDIO
// new Exercise("Elliptical", "CARDIO", "FULL_BODY", "MACHINE", "BEGINNER", 8.0,
// "Full body elliptical", "Resistance 10-12, use arm handles"),
// new Exercise("Swimming", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 11.0,
// "Full body swimming", "Various strokes, focus on breathing"),
// new Exercise("Jump Rope", "CARDIO", "FULL_BODY", "RESISTANCE_BAND",
// "INTERMEDIATE",
// 12.0,
// "Rope skipping", "Start basic, progress to advanced patterns"),

// // ===== CHEST EXERCISES =====

// // BARBELL EXERCISES
// new Exercise("Barbell Bench Press", "STRENGTH", "CHEST", "BARBELL",
// "INTERMEDIATE", 6.0,
// "Flat barbell bench press", "Control weight, full ROM, proper spotter"),
// new Exercise("Barbell Incline Press", "STRENGTH", "CHEST", "BARBELL",
// "INTERMEDIATE",
// 6.5,
// "Incline barbell press", "45-degree angle, emphasize upper chest"),
// new Exercise("Barbell Decline Press", "STRENGTH", "CHEST", "BARBELL",
// "INTERMEDIATE",
// 6.0,
// "Decline barbell press", "Decline angle, focus on lower chest"),
// new Exercise("Smith Machine Bench Press", "STRENGTH", "CHEST", "MACHINE",
// "BEGINNER",
// 5.5,
// "Smith machine chest press", "Controlled movement, safety stops"),

// // DUMBBELL EXERCISES
// new Exercise("Dumbbell Chest Press", "STRENGTH", "CHEST", "DUMBBELL",
// "BEGINNER", 5.5,
// "Flat dumbbell press", "Press up and together, full ROM"),
// new Exercise("Dumbbell Incline Press", "STRENGTH", "CHEST", "DUMBBELL",
// "INTERMEDIATE",
// 6.0,
// "Incline dumbbell press", "45-degree incline, upper chest focus"),
// new Exercise("Dumbbell Decline Press", "STRENGTH", "CHEST", "DUMBBELL",
// "INTERMEDIATE",
// 5.5,
// "Decline dumbbell press", "Decline position, controlled movement"),
// new Exercise("Dumbbell Fly", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE",
// 5.0,
// "Chest isolation fly", "Wide arc motion, chest stretch"),
// new Exercise("Incline Dumbbell Fly", "STRENGTH", "CHEST", "DUMBBELL",
// "INTERMEDIATE",
// 5.0,
// "Incline fly movement", "Upper chest isolation, control stretch"),

// // CABLE & MACHINE
// new Exercise("Cable Crossover", "STRENGTH", "CHEST", "MACHINE",
// "INTERMEDIATE", 5.0,
// "Cable chest crossover", "Cross cables at chest level, squeeze center"),
// new Exercise("Cable Fly", "STRENGTH", "CHEST", "MACHINE", "INTERMEDIATE",
// 4.5,
// "Cable chest fly", "Arc motion, constant tension"),
// new Exercise("Decline Cable Fly", "STRENGTH", "CHEST", "MACHINE",
// "INTERMEDIATE", 4.5,
// "Cable decline fly", "Low to high cable movement"),
// new Exercise("Incline Cable Fly", "STRENGTH", "CHEST", "MACHINE",
// "INTERMEDIATE", 4.5,
// "Cable incline fly", "High to low cable movement"),
// new Exercise("Pec Deck", "STRENGTH", "CHEST", "MACHINE", "BEGINNER", 4.0,
// "Pec deck machine", "Squeeze pecs together, controlled movement"),
// new Exercise("Machine Chest Press", "STRENGTH", "CHEST", "MACHINE",
// "BEGINNER", 5.0,
// "Chest press machine", "Controlled press, full extension"),

// // BODYWEIGHT
// new Exercise("Push-ups", "STRENGTH", "CHEST", "NONE", "BEGINNER", 5.0,
// "Standard push-ups", "Body straight, chest to ground"),
// new Exercise("Incline Push-ups", "STRENGTH", "CHEST", "NONE", "BEGINNER",
// 4.0,
// "Elevated feet push-ups", "Feet on bench, hands on ground"),
// new Exercise("Decline Push-ups", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE",
// 6.0,
// "Hands elevated push-ups", "Hands on bench, feet on ground"),
// new Exercise("Chest Dips", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE", 6.0,
// "Parallel bar dips", "Lower until chest stretch, press up"),
// new Exercise("Decline Dips", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE",
// 6.5,
// "Dips with forward lean", "Lean forward, emphasize chest"),

// // ===== BACK EXERCISES =====

// // PULL-UPS & LAT EXERCISES
// new Exercise("Pull-ups", "STRENGTH", "BACK", "NONE", "INTERMEDIATE", 6.0,
// "Standard pull-ups", "Hang from bar, pull chin over bar"),
// new Exercise("Wide Grip Pull-ups", "STRENGTH", "BACK", "NONE", "ADVANCED",
// 6.5,
// "Wide grip pull-ups", "Wider than shoulder width grip"),
// new Exercise("Close Grip Pull-ups", "STRENGTH", "BACK", "NONE",
// "INTERMEDIATE", 6.0,
// "Narrow grip pull-ups", "Hands closer together"),

// // LAT PULLDOWNS
// new Exercise("Wide Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE",
// "BEGINNER", 5.0,
// "Wide grip lat pulldown", "Pull to upper chest, squeeze lats"),
// new Exercise("Close Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE",
// "INTERMEDIATE",
// 5.5,
// "Close grip pulldown", "V-bar or close grip, pull to chest"),
// new Exercise("V-Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE",
// "INTERMEDIATE", 5.5,
// "V-bar lat pulldown", "V-handle attachment, pull to chest"),
// new Exercise("Reverse Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE",
// "INTERMEDIATE",
// 5.5,
// "Underhand grip pulldown", "Reverse grip, emphasize lower lats"),
// new Exercise("Rope Lat Pulldown", "STRENGTH", "BACK", "MACHINE",
// "INTERMEDIATE", 5.0,
// "Rope attachment pulldown", "Rope handle, wide pull"),
// new Exercise("Single Hand Lat Pulldown", "STRENGTH", "BACK", "MACHINE",
// "INTERMEDIATE",
// 5.0,
// "Unilateral lat pulldown", "One arm at a time, focus on form"),

// // ROWING EXERCISES
// new Exercise("Barbell Rows", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE",
// 5.5,
// "Bent-over barbell rows", "Bend at hips, row to lower chest"),
// new Exercise("Dumbbell Rows", "STRENGTH", "BACK", "DUMBBELL", "BEGINNER",
// 5.0,
// "Single-arm dumbbell row", "Support on bench, row to hip"),
// new Exercise("Cable Rows", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
// "Seated cable rows", "Sit upright, pull to abdomen"),
// new Exercise("Machine Rows", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
// "Machine rowing exercise", "Controlled pulling motion"),
// new Exercise("Rope Rows", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE", 5.0,
// "Cable rope rows", "Rope attachment, separate at chest"),
// new Exercise("Close Grip Machine Rows", "STRENGTH", "BACK", "MACHINE",
// "INTERMEDIATE",
// 5.5,
// "Close grip seated rows", "Narrow grip, squeeze shoulder blades"),
// new Exercise("Wide Grip Barbell Rows", "STRENGTH", "BACK", "BARBELL",
// "INTERMEDIATE",
// 5.5,
// "Wide grip bent rows", "Wider grip, upper back focus"),
// new Exercise("Inverted Rows", "STRENGTH", "BACK", "NONE", "INTERMEDIATE",
// 5.0,
// "Bodyweight inverted rows", "Under bar, pull chest to bar"),

// // OTHER BACK
// new Exercise("Deadlifts", "STRENGTH", "BACK", "BARBELL", "ADVANCED", 7.0,
// "Conventional deadlift", "Hip hinge, keep bar close"),
// new Exercise("Hyperextensions", "STRENGTH", "BACK", "MACHINE", "BEGINNER",
// 4.0,
// "Lower back extensions", "Hyperextension bench, back focus"),
// new Exercise("Dumbbell Pullover", "STRENGTH", "BACK", "DUMBBELL",
// "INTERMEDIATE", 5.0,
// "Chest/back pullover", "Over bench, stretch lats"),

// // TRAPS
// new Exercise("Barbell Shrugs", "STRENGTH", "BACK", "BARBELL", "BEGINNER",
// 4.0,
// "Barbell trap shrugs", "Elevate shoulders, hold contraction"),
// new Exercise("Dumbbell Shrugs", "STRENGTH", "BACK", "DUMBBELL", "BEGINNER",
// 4.0,
// "Dumbbell trap shrugs", "Hold at sides, shrug up"),
// new Exercise("Rope Shrugs", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
// 4.0,
// "Cable rope shrugs", "Rope attachment, shrug motion"),
// new Exercise("Upright Rows", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE",
// 4.5,
// "Barbell upright rows", "Pull bar to chin level"),

// // ===== SHOULDER EXERCISES =====

// // OVERHEAD PRESSES
// new Exercise("Barbell Shoulder Press", "STRENGTH", "SHOULDERS", "BARBELL",
// "INTERMEDIATE", 5.0,
// "Standing shoulder press", "Press barbell overhead"),
// new Exercise("Behind Neck Press", "STRENGTH", "SHOULDERS", "BARBELL",
// "ADVANCED", 5.5,
// "Behind neck shoulder press", "Press from behind neck - advanced only"),
// new Exercise("Dumbbell Shoulder Press", "STRENGTH", "SHOULDERS", "DUMBBELL",
// "BEGINNER",
// 5.0,
// "Seated/standing DB press", "Press dumbbells overhead"),
// new Exercise("Machine Shoulder Press", "STRENGTH", "SHOULDERS", "MACHINE",
// "BEGINNER",
// 4.5,
// "Machine shoulder press", "Controlled overhead press"),
// new Exercise("Single Hand Dumbbell Press", "STRENGTH", "SHOULDERS",
// "DUMBBELL",
// "INTERMEDIATE", 4.5,
// "Unilateral shoulder press", "One arm at a time"),

// // LATERAL RAISES
// new Exercise("Dumbbell Side Raise", "STRENGTH", "SHOULDERS", "DUMBBELL",
// "BEGINNER",
// 3.5,
// "Lateral deltoid raise", "Raise to shoulder height"),
// new Exercise("Cable Side Raise", "STRENGTH", "SHOULDERS", "MACHINE",
// "INTERMEDIATE",
// 4.0,
// "Cable lateral raise", "Constant tension throughout"),
// new Exercise("Machine Side Raise", "STRENGTH", "SHOULDERS", "MACHINE",
// "BEGINNER", 3.5,
// "Machine lateral raise", "Controlled side raise motion"),

// // FRONT RAISES
// new Exercise("Dumbbell Front Raise", "STRENGTH", "SHOULDERS", "DUMBBELL",
// "BEGINNER",
// 3.5,
// "Anterior deltoid raise", "Raise forward to shoulder height"),
// new Exercise("Barbell Front Raise", "STRENGTH", "SHOULDERS", "BARBELL",
// "INTERMEDIATE",
// 4.0,
// "Barbell front raise", "Both arms together"),
// new Exercise("Cable Front Raise", "STRENGTH", "SHOULDERS", "MACHINE",
// "INTERMEDIATE",
// 4.0,
// "Cable front raise", "Cable constant tension"),
// new Exercise("Plate Front Raise", "STRENGTH", "SHOULDERS", "NONE",
// "INTERMEDIATE", 4.0,
// "Weight plate front raise", "Hold plate, raise forward"),

// // REAR DELTOIDS
// new Exercise("Dumbbell Rear Delt Fly", "STRENGTH", "SHOULDERS", "DUMBBELL",
// "INTERMEDIATE", 4.0,
// "Rear deltoid isolation", "Bend forward, raise weights out"),
// new Exercise("Cable Rear Delt Fly", "STRENGTH", "SHOULDERS", "MACHINE",
// "INTERMEDIATE",
// 4.0,
// "Cable rear deltoid fly", "Cross cables behind body"),
// new Exercise("Rear Delt Machine Fly", "STRENGTH", "SHOULDERS", "MACHINE",
// "BEGINNER",
// 3.5,
// "Machine rear delt fly", "Reverse pec deck motion"),
// new Exercise("Laying Rear Delt Fly", "STRENGTH", "SHOULDERS", "DUMBBELL",
// "INTERMEDIATE", 4.0,
// "Prone rear delt fly", "Lie face down, raise weights"),

// // ===== ARM EXERCISES =====

// // BICEPS
// new Exercise("Barbell Curls", "STRENGTH", "ARMS", "BARBELL", "BEGINNER", 3.5,
// "Standing barbell curls", "Curl bar up, squeeze biceps"),
// new Exercise("Dumbbell Bicep Curls", "STRENGTH", "ARMS", "DUMBBELL",
// "BEGINNER", 3.5,
// "Alternating dumbbell curls", "Curl weight up, control descent"),
// new Exercise("Hammer Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
// "Neutral grip curls", "Palms facing each other"),
// new Exercise("Preacher Curls", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE",
// 4.0,
// "Preacher bench curls", "Isolated bicep movement"),
// new Exercise("Cable Curls", "STRENGTH", "ARMS", "MACHINE", "INTERMEDIATE",
// 3.5,
// "Cable bicep curls", "Constant tension curls"),
// new Exercise("Concentration Curls", "STRENGTH", "ARMS", "DUMBBELL",
// "INTERMEDIATE", 3.0,
// "Seated concentration curls", "One arm isolation"),
// new Exercise("Close Grip Barbell Curl", "STRENGTH", "ARMS", "BARBELL",
// "INTERMEDIATE",
// 4.0,
// "Narrow grip barbell curl", "Hands closer together"),
// new Exercise("21s Biceps", "STRENGTH", "ARMS", "BARBELL", "ADVANCED", 5.0,
// "21-rep bicep protocol", "7 bottom + 7 top + 7 full reps"),

// // TRICEPS
// new Exercise("Tricep Dips", "STRENGTH", "ARMS", "NONE", "BEGINNER", 4.0,
// "Parallel bar dips", "Lower and press back up"),
// new Exercise("Bench Dips", "STRENGTH", "ARMS", "NONE", "BEGINNER", 3.5,
// "Bench/chair dips", "Feet on ground, lower body"),
// new Exercise("Skull Crushers", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE",
// 4.5,
// "Lying tricep extension", "Lower to forehead, extend up"),
// new Exercise("Overhead Tricep Extension", "STRENGTH", "ARMS", "DUMBBELL",
// "INTERMEDIATE", 4.0,
// "Seated/standing overhead ext", "Weight behind head, extend up"),
// new Exercise("Tricep Pushdown", "STRENGTH", "ARMS", "MACHINE", "BEGINNER",
// 4.0,
// "Cable tricep pushdown", "Push cable down, squeeze triceps"),
// new Exercise("Close Grip Bench Press", "STRENGTH", "ARMS", "BARBELL",
// "INTERMEDIATE",
// 5.0,
// "Narrow grip bench press", "Emphasize triceps"),
// new Exercise("Tricep Kickbacks", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER",
// 3.5,
// "Bent-over kickbacks", "Extend weight behind body"),
// new Exercise("Rope Overhead Extension", "STRENGTH", "ARMS", "MACHINE",
// "INTERMEDIATE",
// 4.0,
// "Cable rope overhead ext", "Rope attachment overhead"),
// new Exercise("21s Triceps", "STRENGTH", "ARMS", "DUMBBELL", "ADVANCED", 5.0,
// "21-rep tricep protocol", "Varied range of motion reps"),

// // ===== LEG EXERCISES =====

// // QUADS
// new Exercise("Barbell Squats", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE",
// 6.0,
// "Back squats", "Bar on upper back, squat down"),
// new Exercise("Front Squats", "STRENGTH", "LEGS", "BARBELL", "ADVANCED", 6.5,
// "Front-loaded squats", "Bar across front, upright torso"),
// new Exercise("Hack Squats", "STRENGTH", "LEGS", "MACHINE", "INTERMEDIATE",
// 6.0,
// "Hack squat machine", "Controlled squat motion"),
// new Exercise("Leg Press", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 5.5,
// "Leg press machine", "Press weight with legs"),
// new Exercise("Leg Extension", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
// "Quadriceps isolation", "Extend legs, squeeze quads"),
// new Exercise("Goblet Squats", "STRENGTH", "LEGS", "DUMBBELL", "BEGINNER",
// 5.0,
// "Dumbbell goblet squats", "Hold weight at chest, squat"),

// // HAMSTRINGS & GLUTES
// new Exercise("Romanian Deadlifts", "STRENGTH", "LEGS", "BARBELL",
// "INTERMEDIATE", 6.0,
// "RDL hip hinge", "Hinge at hips, feel hamstring stretch"),
// new Exercise("Stiff Leg Deadlifts", "STRENGTH", "LEGS", "DUMBBELL",
// "INTERMEDIATE", 5.5,
// "Straight leg deadlift", "Keep legs relatively straight"),
// new Exercise("Leg Curls", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
// "Hamstring curls", "Curl heels to glutes"),
// new Exercise("Sumo Deadlifts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE",
// 6.5,
// "Wide stance deadlift", "Wide foot placement"),
// new Exercise("Hip Thrusts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE",
// 5.0,
// "Barbell hip thrust", "Drive hips up, squeeze glutes"),
// new Exercise("Glute Bridges", "STRENGTH", "LEGS", "NONE", "BEGINNER", 3.5,
// "Bodyweight hip bridge", "Lie on back, drive hips up"),
// new Exercise("Gluteus Kickbacks", "STRENGTH", "LEGS", "MACHINE", "BEGINNER",
// 3.5,
// "Cable/machine kickbacks", "Kick leg back, squeeze glute"),

// // LUNGES
// new Exercise("Walking Lunges", "STRENGTH", "LEGS", "DUMBBELL",
// "INTERMEDIATE", 5.5,
// "Moving lunge pattern", "Step forward alternating legs"),
// new Exercise("Reverse Lunges", "STRENGTH", "LEGS", "DUMBBELL",
// "INTERMEDIATE", 5.0,
// "Step backward lunges", "Step back into lunge"),
// new Exercise("Side Lunges", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE",
// 4.5,
// "Lateral lunge movement", "Step to side, sit back"),
// new Exercise("Bulgarian Split Squats", "STRENGTH", "LEGS", "DUMBBELL",
// "INTERMEDIATE",
// 5.5,
// "Rear foot elevated lunge", "Back foot on bench"),

// // CALVES
// new Exercise("Standing Calf Raises", "STRENGTH", "LEGS", "MACHINE",
// "BEGINNER", 3.0,
// "Standing calf machine", "Rise on toes, lower slowly"),
// new Exercise("Seated Calf Raises", "STRENGTH", "LEGS", "MACHINE", "BEGINNER",
// 3.0,
// "Seated calf machine", "Calf raises in seated position"),
// new Exercise("Calf Press", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 3.5,
// "Leg press calf variation", "Press with toes on leg press"),

// // ===== CORE/ABS EXERCISES =====

// // UPPER ABS
// new Exercise("Crunches", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.5,
// "Basic abdominal crunches", "Lift shoulders, squeeze abs"),
// new Exercise("Incline Crunches", "STRENGTH", "CORE", "NONE", "BEGINNER", 4.0,
// "Decline bench crunches", "Crunch on incline bench"),
// new Exercise("Cable Crunches", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE",
// 4.5,
// "Kneeling cable crunches", "Crunch against cable resistance"),
// new Exercise("Rope Crunches", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE",
// 4.0,
// "Cable rope crunches", "Rope attachment ab exercise"),
// new Exercise("Sit-ups", "STRENGTH", "CORE", "NONE", "BEGINNER", 4.0,
// "Full sit-up movement", "Full range abdominal exercise"),
// new Exercise("Decline Sit-ups", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 4.5,
// "Decline bench sit-ups", "Increased difficulty sit-ups"),

// // LOWER ABS
// new Exercise("Reverse Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 4.0,
// "Lower ab crunches", "Bring knees to chest"),
// new Exercise("Hanging Knee Raises", "STRENGTH", "CORE", "NONE",
// "INTERMEDIATE", 5.0,
// "Hanging ab exercise", "Hang and raise knees"),
// new Exercise("Leg Raises", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.5,
// "Lying leg raises", "Raise legs from lying position"),
// new Exercise("V-Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 5.0,
// "Double crunch", "Bring knees and shoulders together"),
// new Exercise("Mountain Climbers", "CARDIO", "CORE", "NONE", "INTERMEDIATE",
// 8.0,
// "Dynamic core exercise", "Plank position, alternate knees"),
// new Exercise("Flutter Kicks", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 4.0,
// "Lying flutter kicks", "Small rapid leg movements"),

// // OBLIQUES & ROTATION
// new Exercise("Russian Twists", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 4.0,
// "Seated torso rotation", "Rotate side to side"),
// new Exercise("Wood Choppers", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE",
// 4.5,
// "Cable wood chopper", "Diagonal cutting motion"),
// new Exercise("Side Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 3.5,
// "Oblique crunches", "Crunch to the side"),
// new Exercise("Bicycle Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 4.0,
// "Alternating elbow to knee", "Bicycle pedaling motion"),
// new Exercise("Side Planks", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
// "Lateral plank hold", "Hold side plank position"),
// new Exercise("Oblique Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE",
// 3.5,
// "Side oblique crunches", "Target oblique muscles"),

// // ISOMETRIC CORE
// new Exercise("Planks", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
// "Forearm plank hold", "Hold straight line position"),
// new Exercise("Side Planks", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
// "Side plank holds", "Hold lateral plank position"),
// new Exercise("Dead Bug", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
// "Core stability exercise", "Opposite arm/leg movements"),
// new Exercise("Stomach Vacuum", "STRENGTH", "CORE", "NONE", "ADVANCED", 3.0,
// "Deep abdominal breathing", "Hold vacuum for 5+ seconds"),

// // ===== HIIT & FUNCTIONAL =====

// // HIIT EXERCISES
// new Exercise("Burpees", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 10.0,
// "Full body burpee", "Squat, plank, jump sequence"),
// new Exercise("High Knees", "CARDIO", "LEGS", "NONE", "BEGINNER", 8.0,
// "Running in place", "Bring knees to hip level"),
// new Exercise("Jumping Jacks", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 7.0,
// "Classic jumping jacks", "Jump feet apart, arms up"),
// new Exercise("Box Jumps", "CARDIO", "LEGS", "NONE", "INTERMEDIATE", 9.0,
// "Jump onto box/platform", "Explosive jump up"),
// new Exercise("Battle Ropes", "CARDIO", "FULL_BODY", "RESISTANCE_BAND",
// "INTERMEDIATE",
// 11.0,
// "Heavy rope waves", "Alternate arm wave patterns"),

// // FUNCTIONAL MOVEMENTS
// new Exercise("Kettlebell Swings", "STRENGTH", "FULL_BODY", "DUMBBELL",
// "INTERMEDIATE",
// 7.0,
// "Hip hinge swing", "Swing using hip drive"));
// exerciseRepository.saveAll(exercises);
// }
// }