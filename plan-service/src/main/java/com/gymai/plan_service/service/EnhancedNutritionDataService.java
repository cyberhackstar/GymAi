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

@Service
@Component
public class EnhancedNutritionDataService {

        @Autowired
        private FoodRepository foodRepository;

        @Autowired
        private ExerciseRepository exerciseRepository;

        @PostConstruct
        public void initializeData() {
                if (foodRepository.count() == 0) {
                        initializeFoodData();
                }

                if (exerciseRepository.count() == 0) {
                        initializeExerciseData();
                }
        }

        private void initializeFoodData() {
                List<Food> foods = Arrays.asList(
                                // GRAINS & CEREALS - Breakfast/Lunch
                                new Food("Oats", 389, 16.9, 66.3, 6.9, 10.6, "VEG", "BREAKFAST", "GRAINS"),
                                new Food("Whole Wheat Bread", 247, 12.6, 41.0, 3.4, 7.4, "VEG", "BREAKFAST", "GRAINS"),
                                new Food("Brown Rice", 111, 2.6, 23.0, 0.9, 1.8, "VEG", "LUNCH", "GRAINS"),
                                new Food("Quinoa", 368, 14.1, 64.2, 6.1, 7.0, "VEG", "LUNCH", "GRAINS"),
                                new Food("Whole Wheat Roti", 71, 2.7, 15.2, 0.4, 2.0, "VEG", "LUNCH", "GRAINS"),
                                new Food("Muesli", 352, 9.7, 66.0, 5.9, 7.3, "VEG", "BREAKFAST", "GRAINS"),
                                new Food("Whole Grain Cereal", 379, 13.0, 67.0, 6.0, 10.0, "VEG", "BREAKFAST",
                                                "GRAINS"),
                                new Food("Flattened Rice (Poha)", 76, 1.4, 16.6, 0.2, 0.5, "VEG", "BREAKFAST",
                                                "GRAINS"),
                                new Food("Daliya (Broken Wheat)", 342, 12.0, 69.0, 1.8, 12.5, "VEG", "BREAKFAST",
                                                "GRAINS"),

                                // PROTEINS - Animal & Plant Based
                                new Food("Chicken Breast", 165, 31.0, 0, 3.6, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                                new Food("Grilled Chicken", 239, 27.3, 0, 13.6, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                                new Food("Chicken Drumstick", 172, 28.3, 0, 5.7, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                                new Food("Salmon", 208, 22.0, 0, 12.4, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                                new Food("Fish (General)", 206, 22.0, 0, 12.0, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                                new Food("Eggs", 155, 13.0, 1.1, 11.0, 0, "VEG", "BREAKFAST", "PROTEIN"),
                                new Food("Egg Whites", 17, 3.6, 0.2, 0.1, 0, "VEG", "BREAKFAST", "PROTEIN"),
                                new Food("Whole Egg", 68, 5.5, 0.4, 4.4, 0, "VEG", "BREAKFAST", "PROTEIN"),

                                // PLANT PROTEINS
                                new Food("Lentils (Dal)", 116, 9.0, 20.1, 0.4, 7.9, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Mixed Lentils", 120, 9.5, 20.5, 0.5, 8.0, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Black Lentils", 341, 25.2, 58.9, 1.6, 18.3, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Chickpeas", 164, 8.9, 27.4, 2.6, 7.6, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Black Chickpeas", 378, 20.1, 61.0, 5.3, 17.1, "VEG", "LUNCH", "PROTEIN"),
                                new Food("White Chickpeas", 378, 20.1, 61.0, 5.3, 17.1, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Roasted Chickpeas", 164, 6.0, 27.0, 2.5, 5.0, "VEG", "SNACK", "PROTEIN"),
                                new Food("Kidney Beans (Rajma)", 127, 8.7, 22.8, 0.5, 6.4, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Black Kidney Beans", 132, 8.9, 23.7, 0.5, 8.7, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Paneer", 265, 18.3, 1.2, 20.8, 0, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Fresh Paneer", 265, 18.3, 1.2, 20.8, 0, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Low Fat Paneer", 206, 20.0, 3.0, 12.0, 0, "VEG", "LUNCH", "PROTEIN"),
                                new Food("Tofu", 76, 8.1, 1.9, 4.8, 0.3, "VEGAN", "LUNCH", "PROTEIN"),
                                new Food("Soy Chunks", 345, 52.0, 33.0, 0.5, 13.0, "VEGAN", "LUNCH", "PROTEIN"),
                                new Food("Tempeh", 193, 19.0, 9.4, 11.0, 9.0, "VEGAN", "LUNCH", "PROTEIN"),

                                // DAIRY PRODUCTS
                                new Food("Greek Yogurt", 59, 10.3, 3.6, 0.4, 0, "VEG", "BREAKFAST", "DAIRY"),
                                new Food("Low Fat Yogurt", 59, 10.3, 3.6, 0.4, 0, "VEG", "SNACK", "DAIRY"),
                                new Food("Non Fat Milk", 42, 3.4, 5.0, 0.2, 0, "VEG", "BREAKFAST", "DAIRY"),
                                new Food("Skim Milk", 35, 3.4, 5.0, 0.1, 0, "VEG", "BREAKFAST", "DAIRY"),
                                new Food("Almond Milk", 17, 0.6, 1.5, 1.2, 0.2, "VEGAN", "BREAKFAST", "DAIRY"),
                                new Food("Soy Milk", 54, 3.3, 6.3, 1.8, 0.6, "VEGAN", "BREAKFAST", "DAIRY"),
                                new Food("Cottage Cheese", 98, 11.1, 3.4, 4.3, 0, "VEG", "SNACK", "DAIRY"),

                                // NUTS & SEEDS
                                new Food("Almonds", 579, 21.2, 21.6, 49.9, 12.5, "VEG", "SNACK", "NUTS"),
                                new Food("Walnuts", 654, 15.2, 13.7, 65.2, 6.7, "VEG", "SNACK", "NUTS"),
                                new Food("Cashews", 553, 18.2, 30.2, 43.9, 3.3, "VEG", "SNACK", "NUTS"),
                                new Food("Peanuts", 567, 25.8, 16.1, 49.2, 8.5, "VEG", "SNACK", "NUTS"),
                                new Food("Peanut Butter", 588, 25.1, 19.6, 50.4, 6.0, "VEG", "SNACK", "NUTS"),
                                new Food("Chia Seeds", 486, 17.0, 42.0, 31.0, 34.4, "VEGAN", "BREAKFAST", "NUTS"),
                                new Food("Flax Seeds", 534, 18.3, 28.9, 42.2, 27.3, "VEGAN", "BREAKFAST", "NUTS"),
                                new Food("Sesame Seeds", 573, 17.7, 23.4, 49.7, 11.8, "VEGAN", "BREAKFAST", "NUTS"),
                                new Food("Sunflower Seeds", 584, 20.8, 20.0, 51.5, 8.6, "VEGAN", "SNACK", "NUTS"),

                                // FRUITS - Low & High Sugar
                                new Food("Apple", 52, 0.3, 13.8, 0.2, 2.4, "VEG", "SNACK", "FRUITS"),
                                new Food("Banana", 89, 1.1, 22.8, 0.3, 2.6, "VEG", "BREAKFAST", "FRUITS"),
                                new Food("Orange", 47, 0.9, 11.8, 0.1, 2.4, "VEG", "SNACK", "FRUITS"),
                                new Food("Pear", 57, 0.4, 15.2, 0.1, 3.1, "VEG", "SNACK", "FRUITS"),
                                new Food("Berries (Mixed)", 57, 0.7, 14.0, 0.3, 2.4, "VEG", "SNACK", "FRUITS"),
                                new Food("Strawberries", 32, 0.7, 7.7, 0.3, 2.0, "VEG", "BREAKFAST", "FRUITS"),
                                new Food("Blueberries", 57, 0.7, 14.5, 0.3, 2.4, "VEG", "SNACK", "FRUITS"),
                                new Food("Cranberries (Dried)", 308, 0.1, 82.8, 1.4, 5.3, "VEG", "SNACK", "FRUITS"),
                                new Food("Blackberries", 43, 1.4, 9.6, 0.5, 5.3, "VEG", "SNACK", "FRUITS"),
                                new Food("Raspberries", 52, 1.2, 11.9, 0.7, 6.5, "VEG", "SNACK", "FRUITS"),
                                new Food("Cherries", 50, 1.0, 12.2, 0.3, 1.6, "VEG", "SNACK", "FRUITS"),
                                new Food("Papaya", 43, 0.5, 10.8, 0.3, 1.7, "VEG", "SNACK", "FRUITS"),
                                new Food("Guava", 68, 2.6, 14.3, 1.0, 5.4, "VEG", "SNACK", "FRUITS"),
                                new Food("Melon", 34, 0.8, 8.6, 0.2, 0.9, "VEG", "SNACK", "FRUITS"),
                                new Food("Grapefruit", 42, 0.8, 10.7, 0.1, 1.6, "VEG", "SNACK", "FRUITS"),
                                new Food("Black Grapes", 62, 0.6, 16.0, 0.2, 0.9, "VEG", "SNACK", "FRUITS"),
                                new Food("Pomegranate", 83, 1.7, 18.7, 1.2, 4.0, "VEG", "SNACK", "FRUITS"),
                                new Food("Pineapple", 50, 0.5, 13.1, 0.1, 1.4, "VEG", "SNACK", "FRUITS"),
                                new Food("Avocado", 160, 2.0, 8.5, 14.7, 6.7, "VEG", "LUNCH", "FRUITS"),
                                new Food("Raisins", 299, 3.1, 79.2, 0.5, 3.7, "VEG", "SNACK", "FRUITS"),

                                // VEGETABLES - All Categories
                                new Food("Spinach", 23, 2.9, 3.6, 0.4, 2.2, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Broccoli", 34, 2.8, 6.6, 0.4, 2.6, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Cauliflower", 25, 1.9, 5.0, 0.3, 2.0, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Bell Pepper", 31, 1.0, 7.3, 0.3, 2.5, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Green Bell Pepper", 20, 0.9, 4.6, 0.2, 1.7, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Red Bell Pepper", 31, 1.0, 7.3, 0.3, 2.5, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Carrot", 41, 0.9, 9.6, 0.2, 2.8, "VEG", "SNACK", "VEGETABLES"),
                                new Food("Cucumber", 16, 0.7, 3.6, 0.1, 0.5, "VEG", "SNACK", "VEGETABLES"),
                                new Food("Tomato", 18, 0.9, 3.9, 0.2, 1.2, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Onion", 40, 1.1, 9.3, 0.1, 1.7, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Green Onion", 32, 1.8, 7.3, 0.2, 2.6, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Sweet Potato", 86, 1.6, 20.1, 0.1, 3.0, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Peas", 81, 5.4, 14.5, 0.4, 5.7, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Green Beans", 35, 1.8, 8.0, 0.1, 2.7, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Cabbage", 25, 1.3, 5.8, 0.1, 2.5, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Lettuce", 15, 1.4, 2.9, 0.2, 1.3, "VEG", "LUNCH", "VEGETABLES"),
                                new Food("Mushrooms", 22, 3.1, 3.3, 0.3, 1.0, "VEG", "LUNCH", "VEGETABLES"),

                                // COOKING OILS & FATS
                                new Food("Olive Oil", 884, 0, 0, 100.0, 0, "VEG", "LUNCH", "FATS"),
                                new Food("Coconut Oil", 862, 0, 0, 100.0, 0, "VEGAN", "LUNCH", "FATS"),
                                new Food("Flaxseed Oil", 884, 0, 0, 100.0, 0, "VEGAN", "LUNCH", "FATS"),
                                new Food("Fish Oil", 902, 0, 0, 100.0, 0, "NON_VEG", "LUNCH", "FATS"),

                                // BEVERAGES & DRINKS
                                new Food("Green Tea", 1, 0, 0, 0, 0, "VEG", "BREAKFAST", "BEVERAGES"),
                                new Food("Black Coffee", 2, 0.3, 0, 0, 0, "VEG", "BREAKFAST", "BEVERAGES"),
                                new Food("Indian Tea", 1, 0, 0.7, 0, 0, "VEG", "BREAKFAST", "BEVERAGES"),
                                new Food("Herbal Tea", 1, 0, 0, 0, 0, "VEG", "SNACK", "BEVERAGES"),

                                // SWEETENERS & CONDIMENTS
                                new Food("Honey", 304, 0.3, 82.4, 0, 0.2, "VEG", "BREAKFAST", "CONDIMENTS"),
                                new Food("Stevia", 0, 0, 0, 0, 0, "VEG", "BREAKFAST", "CONDIMENTS"),
                                new Food("Cinnamon", 247, 4.0, 80.6, 1.2, 53.1, "VEG", "BREAKFAST", "SPICES"),
                                new Food("Ketchup", 112, 1.7, 27.4, 0.1, 0.6, "VEG", "LUNCH", "CONDIMENTS"),

                                // SPECIALIZED FOODS FROM GURU MANN PLANS
                                new Food("Protein Cupcake", 295, 34.0, 6.0, 15.0, 2.0, "VEG", "SNACK", "PROTEIN"),
                                new Food("Protein Laddoo", 320, 25.0, 15.0, 18.0, 3.0, "VEG", "SNACK", "PROTEIN"),
                                new Food("Protein Burfi", 279, 25.0, 11.0, 15.0, 2.0, "VEG", "SNACK", "PROTEIN"),

                                // SUPPLEMENTS (Estimated values)
                                new Food("Whey Protein (per scoop)", 120, 24.0, 2.0, 1.0, 0, "VEG", "POST_WORKOUT",
                                                "PROTEIN"),
                                new Food("Casein Protein (per scoop)", 110, 24.0, 3.0, 0.5, 0, "VEG", "BEFORE_BED",
                                                "PROTEIN"),
                                new Food("BCAA", 10, 2.5, 0, 0, 0, "VEG", "PRE_WORKOUT", "SUPPLEMENTS"),
                                new Food("Creatine", 0, 0, 0, 0, 0, "VEG", "PRE_WORKOUT", "SUPPLEMENTS"),
                                new Food("Multivitamin", 5, 0, 1.0, 0, 0, "VEG", "BREAKFAST", "SUPPLEMENTS"));

                foodRepository.saveAll(foods);
        }

        private void initializeExerciseData() {
                List<Exercise> exercises = Arrays.asList(
                                // CARDIO EXERCISES
                                new Exercise("Running", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 10.0,
                                                "High-intensity running exercise",
                                                "Start with 5min warm-up at 2.8mph, maintain 6-10mph pace, focus on breathing"),
                                new Exercise("Walking", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 4.0,
                                                "Brisk walking exercise",
                                                "Maintain 3-4mph pace, swing arms naturally, outdoor or treadmill"),
                                new Exercise("Cycling", "CARDIO", "LEGS", "MACHINE", "BEGINNER", 8.0,
                                                "Stationary or outdoor cycling",
                                                "Maintain steady cadence, adjust resistance level 10-12"),
                                new Exercise("Elliptical", "CARDIO", "FULL_BODY", "MACHINE", "BEGINNER", 7.0,
                                                "Full body elliptical workout",
                                                "Maintain resistance 10-12, use arm handles for full body"),
                                new Exercise("Stationary Bike", "CARDIO", "LEGS", "MACHINE", "BEGINNER", 6.0,
                                                "Indoor cycling workout",
                                                "Set level 10-12, maintain steady pace for 20-30 minutes"),
                                new Exercise("Jump Rope", "CARDIO", "FULL_BODY", "RESISTANCE_BAND", "INTERMEDIATE",
                                                12.0,
                                                "High-intensity skipping",
                                                "Start with basic bounce, keep jumps low, build endurance"),
                                new Exercise("Swimming", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 11.0,
                                                "Full body swimming workout",
                                                "Focus on form and breathing technique, various strokes"),
                                new Exercise("HIIT Treadmill", "CARDIO", "FULL_BODY", "MACHINE", "ADVANCED", 15.0,
                                                "High intensity interval training",
                                                "30sec sprint/30sec rest cycles, increase speed progressively"),

                                // CHEST EXERCISES
                                new Exercise("Push-ups", "STRENGTH", "CHEST", "NONE", "BEGINNER", 5.0,
                                                "Classic bodyweight chest exercise",
                                                "Keep body straight, lower chest to ground, full range of motion"),
                                new Exercise("Barbell Bench Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE", 6.0,
                                                "Heavy compound chest exercise",
                                                "Control the weight, full range of motion, proper spotter"),
                                new Exercise("Incline Barbell Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE",
                                                6.5,
                                                "Upper chest development",
                                                "45-degree incline, controlled movement, focus on upper pecs"),
                                new Exercise("Decline Barbell Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE",
                                                6.0,
                                                "Lower chest exercise",
                                                "Decline angle, control weight, emphasize lower chest"),
                                new Exercise("Dumbbell Chest Press", "STRENGTH", "CHEST", "DUMBBELL", "BEGINNER", 5.5,
                                                "Dumbbell chest press",
                                                "Press dumbbells up and together, full range of motion"),
                                new Exercise("Dumbbell Incline Press", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE",
                                                6.0,
                                                "Incline dumbbell press", "45-degree incline, press up and together"),
                                new Exercise("Dumbbell Decline Press", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE",
                                                5.5,
                                                "Decline dumbbell press", "Decline position, controlled movement"),
                                new Exercise("Dumbbell Fly", "STRENGTH", "CHEST", "DUMBBELL", "INTERMEDIATE", 5.0,
                                                "Chest isolation exercise", "Wide arc motion, feel stretch in chest"),
                                new Exercise("Cable Crossover", "STRENGTH", "CHEST", "MACHINE", "INTERMEDIATE", 5.0,
                                                "Cable chest exercise",
                                                "Cross cables at chest level, squeeze at center"),
                                new Exercise("Cable Fly", "STRENGTH", "CHEST", "MACHINE", "INTERMEDIATE", 4.5,
                                                "Cable chest fly",
                                                "Controlled arc motion, maintain slight bend in elbows"),
                                new Exercise("Chest Dips", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE", 6.0,
                                                "Parallel bar dips",
                                                "Lower body until chest stretch, press up explosively"),
                                new Exercise("Decline Dips", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE", 6.5,
                                                "Decline position dips", "Feet elevated, emphasize chest development"),

                                // BACK EXERCISES
                                new Exercise("Pull-ups", "STRENGTH", "BACK", "NONE", "INTERMEDIATE", 6.0,
                                                "Classic back exercise",
                                                "Hang from bar, pull body up until chin over bar"),
                                new Exercise("Bent-over Row", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE", 5.5,
                                                "Barbell rowing exercise",
                                                "Bend at hips, row bar to lower chest, squeeze shoulder blades"),
                                new Exercise("Dumbbell Rows", "STRENGTH", "BACK", "DUMBBELL", "BEGINNER", 5.0,
                                                "Single-arm dumbbell row",
                                                "Support on bench, row weight to hip, squeeze back"),
                                new Exercise("Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
                                                "Lat pulldown machine",
                                                "Pull bar down to upper chest, squeeze shoulder blades"),
                                new Exercise("Wide Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
                                                5.5,
                                                "Wide grip lat exercise",
                                                "Wide grip, pull to upper chest, focus on lats"),
                                new Exercise("Close Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE",
                                                5.5,
                                                "Close grip variation",
                                                "Narrow grip, pull to chest, emphasize middle back"),
                                new Exercise("V-Grip Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE", 5.5,
                                                "V-bar lat pulldown", "Use V-bar attachment, pull to chest"),
                                new Exercise("Machine Rows", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
                                                "Seated cable rows",
                                                "Sit upright, pull handle to abdomen, squeeze back"),
                                new Exercise("Rope Rows", "STRENGTH", "BACK", "MACHINE", "INTERMEDIATE", 5.0,
                                                "Cable rope rows",
                                                "Use rope attachment, pull to chest, separate rope at end"),
                                new Exercise("Deadlift", "STRENGTH", "BACK", "BARBELL", "ADVANCED", 7.0,
                                                "Compound deadlift exercise",
                                                "Hip hinge movement, keep bar close to body, full body power"),
                                new Exercise("Hyperextension", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 4.0,
                                                "Lower back extension",
                                                "Use hyperextension bench, focus on lower back"),
                                new Exercise("Shrugs", "STRENGTH", "BACK", "BARBELL", "BEGINNER", 4.0,
                                                "Barbell shrugs",
                                                "Elevate shoulders, hold contraction, control descent"),
                                new Exercise("Dumbbell Shrugs", "STRENGTH", "BACK", "DUMBBELL", "BEGINNER", 4.0,
                                                "Dumbbell trap exercise",
                                                "Hold dumbbells at sides, shrug shoulders up"),

                                // SHOULDER EXERCISES
                                new Exercise("Overhead Press", "STRENGTH", "SHOULDERS", "BARBELL", "INTERMEDIATE", 5.0,
                                                "Standing shoulder press",
                                                "Press barbell overhead from shoulder height"),
                                new Exercise("Dumbbell Shoulder Press", "STRENGTH", "SHOULDERS", "DUMBBELL", "BEGINNER",
                                                5.0,
                                                "Seated or standing press",
                                                "Press dumbbells overhead, control the movement"),
                                new Exercise("Dumbbell Side Raise", "STRENGTH", "SHOULDERS", "DUMBBELL", "BEGINNER",
                                                3.5,
                                                "Lateral deltoid isolation",
                                                "Raise dumbbells to shoulder height, control descent"),
                                new Exercise("Dumbbell Front Raise", "STRENGTH", "SHOULDERS", "DUMBBELL", "BEGINNER",
                                                3.5,
                                                "Anterior deltoid exercise",
                                                "Raise dumbbell forward to shoulder height"),
                                new Exercise("Dumbbell Rear Delt Fly", "STRENGTH", "SHOULDERS", "DUMBBELL",
                                                "INTERMEDIATE", 4.0,
                                                "Posterior deltoid isolation",
                                                "Bend forward, raise weights out to sides"),
                                new Exercise("Cable Side Raise", "STRENGTH", "SHOULDERS", "MACHINE", "INTERMEDIATE",
                                                4.0,
                                                "Cable lateral raise",
                                                "Use cable for constant tension throughout movement"),
                                new Exercise("Cable Front Raise", "STRENGTH", "SHOULDERS", "MACHINE", "INTERMEDIATE",
                                                4.0,
                                                "Cable front deltoid exercise",
                                                "Raise cable handle forward to shoulder height"),
                                new Exercise("Cable Rear Delt Fly", "STRENGTH", "SHOULDERS", "MACHINE", "INTERMEDIATE",
                                                4.0,
                                                "Cable rear deltoid fly",
                                                "Cross cables behind body, squeeze rear delts"),
                                new Exercise("Upright Rows", "STRENGTH", "SHOULDERS", "BARBELL", "INTERMEDIATE", 4.5,
                                                "Barbell upright row", "Pull bar up along body to chin level"),

                                // ARM EXERCISES - BICEPS
                                new Exercise("Bicep Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
                                                "Basic bicep exercise",
                                                "Curl weight up, squeeze bicep at top, control descent"),
                                new Exercise("Barbell Curls", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE", 4.0,
                                                "Barbell bicep curls", "Stand upright, curl bar up, squeeze biceps"),
                                new Exercise("Hammer Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
                                                "Neutral grip curls",
                                                "Keep palms facing each other throughout movement"),
                                new Exercise("Preacher Curls", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE", 4.0,
                                                "Preacher bench curls", "Use preacher bench, focus on bicep isolation"),
                                new Exercise("Cable Curls", "STRENGTH", "ARMS", "MACHINE", "INTERMEDIATE", 3.5,
                                                "Cable bicep curls", "Use cable machine for constant tension"),
                                new Exercise("Concentration Curls", "STRENGTH", "ARMS", "DUMBBELL", "INTERMEDIATE", 3.0,
                                                "Isolated bicep curls",
                                                "Sit and curl one arm at a time, focus on form"),

                                // ARM EXERCISES - TRICEPS
                                new Exercise("Tricep Dips", "STRENGTH", "ARMS", "NONE", "BEGINNER", 4.0,
                                                "Chair or bench dips", "Lower body by bending arms, press back up"),
                                new Exercise("Overhead Tricep Extension", "STRENGTH", "ARMS", "DUMBBELL",
                                                "INTERMEDIATE", 4.0,
                                                "Dumbbell overhead extension",
                                                "Hold weight overhead, lower behind head, extend back up"),
                                new Exercise("Tricep Pushdown", "STRENGTH", "ARMS", "MACHINE", "BEGINNER", 4.0,
                                                "Cable tricep pushdown", "Push cable down, squeeze triceps at bottom"),
                                new Exercise("Skull Crushers", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE", 4.5,
                                                "Lying tricep extension", "Lower bar to forehead, extend back up"),
                                new Exercise("Close Grip Bench Press", "STRENGTH", "ARMS", "BARBELL", "INTERMEDIATE",
                                                5.0,
                                                "Narrow grip bench press", "Hands close together, emphasize triceps"),
                                new Exercise("Tricep Kickbacks", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
                                                "Dumbbell kickback exercise",
                                                "Bend forward, extend weight behind body"),
                                new Exercise("Rope Overhead Extension", "STRENGTH", "ARMS", "MACHINE", "INTERMEDIATE",
                                                4.0,
                                                "Cable rope overhead extension",
                                                "Use rope attachment, extend overhead"),

                                // LEG EXERCISES - QUADS & GLUTES
                                new Exercise("Squats", "STRENGTH", "LEGS", "NONE", "BEGINNER", 5.0,
                                                "Bodyweight squats",
                                                "Feet shoulder-width apart, lower hips back and down"),
                                new Exercise("Barbell Squats", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 6.0,
                                                "Back squat with barbell",
                                                "Bar on upper back, squat down keeping chest up"),
                                new Exercise("Front Squats", "STRENGTH", "LEGS", "BARBELL", "ADVANCED", 6.5,
                                                "Front-loaded barbell squat",
                                                "Bar across front shoulders, maintain upright torso"),
                                new Exercise("Leg Press", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 5.5,
                                                "Leg press machine", "Press weight with legs, full range of motion"),
                                new Exercise("Lunges", "STRENGTH", "LEGS", "NONE", "BEGINNER", 4.5,
                                                "Forward lunges", "Step forward, lower back knee toward ground"),
                                new Exercise("Dumbbell Lunges", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE", 5.0,
                                                "Weighted lunges", "Hold dumbbells, step forward into lunge position"),
                                new Exercise("Walking Lunges", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE", 5.5,
                                                "Moving lunge exercise",
                                                "Step forward alternating legs, continuous motion"),
                                new Exercise("Leg Extension", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
                                                "Quadriceps isolation",
                                                "Extend legs against resistance, squeeze quads"),
                                new Exercise("Bulgarian Split Squats", "STRENGTH", "LEGS", "DUMBBELL", "INTERMEDIATE",
                                                5.5,
                                                "Single leg squat variation",
                                                "Rear foot elevated, squat down on front leg"),

                                // LEG EXERCISES - HAMSTRINGS & GLUTES
                                new Exercise("Deadlifts", "STRENGTH", "LEGS", "BARBELL", "ADVANCED", 7.0,
                                                "Hip hinge deadlift",
                                                "Hinge at hips, keep bar close, drive through heels"),
                                new Exercise("Romanian Deadlifts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 6.0,
                                                "Stiff leg deadlift variation",
                                                "Keep legs relatively straight, hinge at hips"),
                                new Exercise("Dumbbell Stiff Leg Deadlift", "STRENGTH", "LEGS", "DUMBBELL",
                                                "INTERMEDIATE", 5.5,
                                                "Dumbbell hamstring exercise",
                                                "Hold dumbbells, hinge forward keeping legs straight"),
                                new Exercise("Leg Curls", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 4.0,
                                                "Hamstring isolation", "Curl heels toward glutes, squeeze hamstrings"),
                                new Exercise("Glute Bridges", "STRENGTH", "LEGS", "NONE", "BEGINNER", 3.5,
                                                "Hip bridge exercise", "Lie on back, drive hips up, squeeze glutes"),
                                new Exercise("Hip Thrusts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 5.0,
                                                "Barbell hip thrust", "Shoulders on bench, drive hips up with weight"),
                                new Exercise("Sumo Deadlifts", "STRENGTH", "LEGS", "BARBELL", "INTERMEDIATE", 6.5,
                                                "Wide stance deadlift",
                                                "Wide foot placement, emphasize glutes and inner thighs"),

                                // CALF EXERCISES
                                new Exercise("Calf Raises", "STRENGTH", "LEGS", "NONE", "BEGINNER", 3.0,
                                                "Standing calf raises", "Rise up on toes, lower slowly, feel stretch"),
                                new Exercise("Seated Calf Raises", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 3.0,
                                                "Seated calf exercise", "Use calf raise machine, full range of motion"),
                                new Exercise("Calf Press", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 3.5,
                                                "Leg press calf variation", "Use leg press machine, press with toes"),

                                // CORE/ABS EXERCISES
                                new Exercise("Plank", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
                                                "Forearm plank hold", "Hold straight line from head to heels"),
                                new Exercise("Crunches", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.5,
                                                "Basic abdominal crunches", "Lift shoulders off ground, squeeze abs"),
                                new Exercise("Incline Crunches", "STRENGTH", "CORE", "MACHINE", "BEGINNER", 4.0,
                                                "Decline bench crunches",
                                                "Use incline bench, crunch up against gravity"),
                                new Exercise("Reverse Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
                                                "Lower ab exercise", "Bring knees to chest, focus on lower abs"),
                                new Exercise("Hanging Knee Raises", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 5.0,
                                                "Hanging ab exercise", "Hang from bar, raise knees to chest"),
                                new Exercise("Russian Twists", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
                                                "Rotational core exercise", "Sit and rotate torso side to side"),
                                new Exercise("Mountain Climbers", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 8.0,
                                                "Dynamic core exercise",
                                                "Plank position, alternate bringing knees to chest"),
                                new Exercise("Cable Crunches", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE", 4.5,
                                                "Cable ab exercise", "Kneel and crunch down against cable resistance"),
                                new Exercise("V-Crunches", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 5.0,
                                                "Double crunch exercise",
                                                "Bring knees and shoulders together simultaneously"),
                                new Exercise("Side Planks", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
                                                "Lateral core stability", "Hold side plank position, engage obliques"),
                                new Exercise("Wood Choppers", "STRENGTH", "CORE", "MACHINE", "INTERMEDIATE", 4.5,
                                                "Rotational cable exercise",
                                                "Use cable, rotate across body like chopping wood"),
                                new Exercise("Dead Bug", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
                                                "Core stability exercise",
                                                "Lie on back, alternate opposite arm and leg movements"),

                                // HIIT EXERCISES
                                new Exercise("Burpees", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 10.0,
                                                "Full body explosive exercise",
                                                "Squat, jump back to plank, jump forward, jump up"),
                                new Exercise("High Knees", "CARDIO", "LEGS", "NONE", "BEGINNER", 8.0,
                                                "Running in place",
                                                "Bring knees up to hip level alternately, quick pace"),
                                new Exercise("Jumping Jacks", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 7.0,
                                                "Classic jumping jacks", "Jump feet apart while raising arms overhead"),
                                new Exercise("Box Jumps", "CARDIO", "LEGS", "NONE", "INTERMEDIATE", 9.0,
                                                "Plyometric box exercise", "Jump onto box, step down, repeat"),
                                new Exercise("Battle Ropes", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 11.0,
                                                "High-intensity rope exercise",
                                                "Alternate arms creating waves with heavy ropes"),
                                new Exercise("Sprint Intervals", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 12.0,
                                                "High-intensity running", "30 seconds sprint, 30 seconds rest cycles"),

                                // FLEXIBILITY/YOGA EXERCISES
                                new Exercise("Yoga Flow", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 3.0,
                                                "Basic yoga sequence", "Flow through poses with controlled breathing"),
                                new Exercise("Static Stretching", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 2.0,
                                                "Hold stretches for flexibility",
                                                "Hold each stretch for 15-30 seconds"),
                                new Exercise("Dynamic Stretching", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 3.0,
                                                "Moving stretches", "Controlled movements through range of motion"),
                                new Exercise("Hip Stretches", "FLEXIBILITY", "LEGS", "NONE", "BEGINNER", 2.5,
                                                "Hip mobility exercises",
                                                "Various stretches to improve hip flexibility"),
                                new Exercise("Shoulder Stretches", "FLEXIBILITY", "SHOULDERS", "NONE", "BEGINNER", 2.0,
                                                "Shoulder mobility", "Stretches to improve shoulder flexibility"),

                                // FUNCTIONAL EXERCISES
                                new Exercise("Farmer's Walk", "STRENGTH", "FULL_BODY", "DUMBBELL", "INTERMEDIATE", 5.0,
                                                "Loaded carry exercise",
                                                "Hold heavy weights and walk for distance/time"),
                                new Exercise("Turkish Get-Up", "STRENGTH", "FULL_BODY", "DUMBBELL", "ADVANCED", 6.0,
                                                "Complex movement pattern",
                                                "Rise from lying to standing while holding weight overhead"),
                                new Exercise("Kettlebell Swings", "STRENGTH", "FULL_BODY", "DUMBBELL", "INTERMEDIATE",
                                                7.0,
                                                "Hip hinge power exercise",
                                                "Swing kettlebell using hip drive, not arms"),
                                new Exercise("Medicine Ball Slams", "STRENGTH", "FULL_BODY", "NONE", "INTERMEDIATE",
                                                8.0,
                                                "Explosive full body exercise",
                                                "Lift ball overhead, slam down with full force"),

                                // WARM-UP EXERCISES
                                new Exercise("Arm Circles", "FLEXIBILITY", "SHOULDERS", "NONE", "BEGINNER", 2.0,
                                                "Shoulder warm-up", "Small to large circular motions with arms"),
                                new Exercise("Leg Swings", "FLEXIBILITY", "LEGS", "NONE", "BEGINNER", 2.5,
                                                "Hip mobility warm-up", "Swing legs forward/back and side to side"),
                                new Exercise("Torso Twists", "FLEXIBILITY", "CORE", "NONE", "BEGINNER", 2.0,
                                                "Spinal mobility", "Gentle rotation of torso while standing"),
                                new Exercise("Light Jogging", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 5.0,
                                                "Cardiovascular warm-up", "Easy pace jogging to increase heart rate"),

                                // ADVANCED COMPOUND MOVEMENTS
                                new Exercise("Clean and Press", "STRENGTH", "FULL_BODY", "BARBELL", "ADVANCED", 8.0,
                                                "Olympic lifting movement", "Clean bar to shoulders, press overhead"),
                                new Exercise("Snatch", "STRENGTH", "FULL_BODY", "BARBELL", "ADVANCED", 9.0,
                                                "Olympic lift", "Lift bar from floor to overhead in one motion"),
                                new Exercise("Thrusters", "STRENGTH", "FULL_BODY", "BARBELL", "ADVANCED", 7.5,
                                                "Squat to press combination", "Front squat into overhead press"),
                                new Exercise("Man Makers", "STRENGTH", "FULL_BODY", "DUMBBELL", "ADVANCED", 9.0,
                                                "Complex full body exercise",
                                                "Burpee with dumbbell rows and overhead press"));

                exerciseRepository.saveAll(exercises);
        }

        // Additional helper methods for specific dietary requirements

        /**
         * Get foods suitable for diabetes management
         */
        // public List<Food> getDiabeticFriendlyFoods() {
        // // Low glycemic index foods based on the diabetes nutrition plan
        // return foodRepository.findByFoodTypeInAndCategoryNotIn(
        // Arrays.asList("VEG", "VEGAN", "NON_VEG"),
        // Arrays.asList("High sugar fruits", "Refined grains")
        // );
        // }

        /**
         * Get foods for cholesterol management
         */
        // public List<Food> getCholesterolFriendlyFoods() {
        // // Based on cholesterol management plan from the PDFs
        // return foodRepository.findByFoodTypeInAndCategoryIn(
        // Arrays.asList("VEG", "VEGAN"),
        // Arrays.asList("NUTS", "VEGETABLES", "FRUITS", "GRAINS", "PROTEIN")
        // );
        // }

        /**
         * Get high protein foods for muscle building
         */
        // public List<Food> getHighProteinFoods() {
        // // Foods with protein > 15g per 100g
        // return foodRepository.findByProteinGreaterThan(15.0);
        // }

        /**
         * Get low calorie foods for weight loss
         */
        // public List<Food> getLowCalorieFoods() {
        // // Foods with calories < 100 per 100g
        // return foodRepository.findByCaloriesLessThan(100.0);
        // }
}
